package org.hildan.minecraft.mining.optimizer.patterns.generated;

import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.geometry.Position;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.Action;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.DigAction;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.DigRange3D;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.MoveAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a current state while digging a sample.
 */
class DiggingState {

    private static final Collection<Action> allActions;

    static {
        Collection<Action> actions = new HashSet<>();
        actions.addAll(MoveAction.getAll());
        actions.addAll(DigAction.getAll(DigRange3D.STRICT));
        allActions = Collections.unmodifiableCollection(actions);
    }

    private final Position currentHeadPosition;

    private final Sample sample;

    private final List<Action> actionsPerformed;

    public DiggingState(Position currentHeadPosition, Sample sample) {
        this.currentHeadPosition = currentHeadPosition;
        this.sample = sample;
        this.actionsPerformed = new ArrayList<>();
    }

    private DiggingState(Position currentHeadPosition, Sample sample, List<Action> actionsPerformed) {
        this.currentHeadPosition = currentHeadPosition;
        this.sample = sample;
        this.actionsPerformed = new ArrayList<>(actionsPerformed);
    }

    private DiggingState transition(Action action) {
        Sample newSample = new Sample(sample);
        Position newHeadPosition = action.executeOn(newSample, currentHeadPosition);
        DiggingState newState = new DiggingState(newHeadPosition, newSample, actionsPerformed);
        newState.actionsPerformed.add(action);
        return newState;
    }

    public Collection<DiggingState> expand() {
        return allActions.stream()
                         .filter(a -> a.isValidFor(sample, currentHeadPosition))
                         .map(this::transition)
                         .collect(Collectors.toList());
    }

    public DiggingPattern toPattern() {

        // FIXME find a way to give these arguments...

        return new GeneratedPattern(null, 0, 0, 0);
    }
}
