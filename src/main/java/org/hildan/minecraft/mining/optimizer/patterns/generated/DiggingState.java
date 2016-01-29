package org.hildan.minecraft.mining.optimizer.patterns.generated;

import org.hildan.minecraft.mining.optimizer.blocks.Sample;
import org.hildan.minecraft.mining.optimizer.geometry.Position;
import org.hildan.minecraft.mining.optimizer.patterns.Access;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.Action;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.DigAction;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.DigRange3D;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.MoveAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a state while digging a sample. It contains the state of the sample, the position of the player and the
 * actions done so far to arrive at this state. It can generate the next possible states from this point using {@link
 * #expand}.
 */
class DiggingState {

    private static final Collection<Action> allActions;

    static {
        Collection<Action> actions = new HashSet<>();
        actions.addAll(MoveAction.getAll());
        actions.addAll(DigAction.getAll(DigRange3D.STRICT));
        allActions = Collections.unmodifiableCollection(actions);
    }

    private final Sample sample;

    private final Map<Access, Position> headPositionsPerAccess;

    private final Map<Access, List<Action>> actionsPerAccess;

    /**
     * Creates an initial state for the given sample based on the given accesses.
     *
     * @param sample
     *         the current sample
     * @param accesses
     *         the list of accesses to start digging
     */
    DiggingState(Sample sample, Collection<Access> accesses) {
        this.sample = sample;
        this.headPositionsPerAccess = new HashMap<>(accesses.size());
        this.actionsPerAccess = new HashMap<>(accesses.size());
        for (Access access : accesses) {
            Position startingHeadPosition = access.above();
            sample.digBlock(access);
            sample.digBlock(startingHeadPosition);
            headPositionsPerAccess.put(access, startingHeadPosition);
            actionsPerAccess.put(access, new ArrayList<>(10));
        }
    }

    /**
     * Creates a copy of the given state.
     *
     * @param state
     *         the state to copy
     */
    private DiggingState(DiggingState state) {
        this.sample = new Sample(state.sample);
        this.headPositionsPerAccess = new HashMap<>(state.headPositionsPerAccess);
        this.actionsPerAccess = new HashMap<>(state.actionsPerAccess.keySet().size());
        for (Entry<Access, List<Action>> entry : state.actionsPerAccess.entrySet()) {
            actionsPerAccess.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    /**
     * Returns the {@code DiggingState} resulting of the execution of the given action on this state. This method does
     * not affect this state.
     *
     * @param access
     *         the access for which to add the action
     * @param action
     *         the action to perform on this state
     * @return the resulting state
     */
    private DiggingState transition(Access access, Action action) {
        DiggingState newState = new DiggingState(this);

        Position newHeadPosition = action.executeOn(newState.sample, newState.headPositionsPerAccess.get(access));
        newState.headPositionsPerAccess.put(access, newHeadPosition);
        newState.actionsPerAccess.get(access).add(action);

        return newState;
    }

    /**
     * Returns whether it is acceptable to execute this action in the current situation.
     *
     * @param access
     *         the access for which to execute the action
     * @param action
     *         the action to perform
     * @return true if this action may be performed in the current situation
     */
    private boolean isAcceptable(Access access, Action action) {
        List<Action> actions = actionsPerAccess.get(access);
        if (!actions.isEmpty()) {
            Action lastAction = actions.get(actions.size() - 1);
            if (action.isInverseOf(lastAction)) {
                return false;
            }
        }
        Position currentHeadPosition = headPositionsPerAccess.get(access);
        return action.isValidFor(sample, currentHeadPosition);
    }

    /**
     * Expands the current state by performing every possible action on it. This method does not affect this state.
     *
     * @return the collection of all states resulting of the execution of each possible action on this state.
     */
    Collection<DiggingState> expand(GenerationConstraints constraints) {
        long actionsCount = actionsPerAccess.values().stream().mapToLong(List::size).sum();
        if (actionsCount >= constraints.getMaxActions()) {
            return new ArrayList<>();
        }
        if (sample.getDugBlocksCount() >= constraints.getMaxDugBlocks()) {
            return new ArrayList<>();
        }
        return headPositionsPerAccess.keySet().stream().flatMap(this::expandAccess).collect(Collectors.toList());
    }

    private Stream<DiggingState> expandAccess(Access access) {
        return allActions.stream()
                         .filter(action -> isAcceptable(access, action))
                         .map(action -> transition(access, action));
    }

    /**
     * Returns whether this state is canonical. This means that we can't remove any of the last actions without changing
     * the resulting pattern. In other words, for every access, the last action must be a dig action, not a move.
     *
     * @return true if this state is canonical
     */
    boolean isCanonical() {
        return actionsPerAccess.values()
                               .stream()
                               .filter(l -> !l.isEmpty())
                               .map(l -> l.get(l.size() - 1))
                               .allMatch(a -> a instanceof DigAction);
    }

    /**
     * Creates a {@link GeneratedPattern} that brings any sample to this state.
     *
     * @return a {@link GeneratedPattern} that brings any sample to this state.
     */
    DiggingPattern toPattern() {
        return new GeneratedPattern(actionsPerAccess, sample.getWidth(), sample.getHeight(), sample.getLength());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        DiggingState state = (DiggingState) obj;

        if (!headPositionsPerAccess.equals(state.headPositionsPerAccess)) {
            return false;
        }
        return sample.equals(state.sample);
    }

    @Override
    public int hashCode() {
        int result = sample.hashCode();
        result = 31 * result + headPositionsPerAccess.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final String indent = "   ";
        for (Entry<Access, List<Action>> entry : actionsPerAccess.entrySet()) {
            sb.append(entry.getKey()).append(String.format("%n"));
            for (Action action : entry.getValue()) {
                sb.append(indent).append(action).append(String.format("%n"));
            }
        }
        return sb.toString();
    }
}
