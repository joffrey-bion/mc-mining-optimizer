package org.hildan.minecraft.mining.optimizer.patterns.generated;

import org.hildan.minecraft.mining.optimizer.chunks.Sample;
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
import java.util.stream.Collectors;

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
        this(sample, new HashMap<>(accesses.size()), new HashMap<>(accesses.size()));
        for (Access access : accesses) {
            Position startingHeadPosition = access.above();
            sample.digBlock(access);
            sample.digBlock(startingHeadPosition);
            headPositionsPerAccess.put(access, startingHeadPosition);
            actionsPerAccess.put(access, new ArrayList<>(10));
        }
    }

    /**
     * Creates a new state with the given sample, player's position, and list of actions performed so far.
     *
     * @param sample
     *         the current sample
     * @param headPositionsPerAccess
     *         the current position of the head of the player for each access
     * @param actionsPerAccess
     */
    private DiggingState(Sample sample, Map<Access, Position> headPositionsPerAccess,
                         Map<Access, List<Action>> actionsPerAccess) {
        this.sample = sample;
        this.headPositionsPerAccess = headPositionsPerAccess;
        this.actionsPerAccess = actionsPerAccess;
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
        Sample newSample = new Sample(sample);

        Map<Access, Position> newHeadPositions = new HashMap<>(headPositionsPerAccess);
        Position newHeadPosition = action.executeOn(newSample, headPositionsPerAccess.get(access));
        newHeadPositions.put(access, newHeadPosition);

        Map<Access, List<Action>> newActions = new HashMap<>(actionsPerAccess);
        newActions.get(access).add(action);

        return new DiggingState(newSample, newHeadPositions, newActions);
    }

    /**
     * Expands the current state by performing every possible action on it. This method does not affect this state.
     *
     * @return the collection of all states resulting of the execution of each possible action on this state.
     */
    Collection<DiggingState> expand() {
        return headPositionsPerAccess.keySet()
                                     .stream()
                                     .flatMap(access -> allActions.stream()
                                                                  .filter(action -> action.isValidFor(sample,
                                                                          headPositionsPerAccess.get(access)))
                                                                  .map(action -> transition(access, action)))
                                     .collect(Collectors.toList());
    }

    /**
     * Creates a {@link GeneratedPattern} that brings any sample to this state.
     *
     * @return a {@link GeneratedPattern} that brings any sample to this state.
     */
    DiggingPattern toPattern() {

        // FIXME find a way to give these as arguments...

        return new GeneratedPattern(actionsPerAccess, sample.getWidth(), sample.getHeight(), sample.getLength());
    }
}
