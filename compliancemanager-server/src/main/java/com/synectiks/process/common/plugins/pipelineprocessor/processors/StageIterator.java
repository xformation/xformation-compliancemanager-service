/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.processors;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ArrayListMultimap;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.Pipeline;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.Stage;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class StageIterator extends AbstractIterator<List<Stage>> {

    private final Configuration config;

    // the currentStage is always one before the next one to be returned
    private int currentStage;


    public StageIterator(Configuration config) {
        this.config = config;
        currentStage = config.initialStage();
    }

    public StageIterator(Set<Pipeline> pipelines) {
        this.config = new Configuration(pipelines);
        currentStage = config.initialStage();
    }

    @Override
    protected List<Stage> computeNext() {
        if (currentStage == config.lastStage()) {
            return endOfData();
        }
        do {
            currentStage++;
            if (currentStage > config.lastStage()) {
                return endOfData();
            }
        } while (!config.hasStages(currentStage));
        return config.getStages(currentStage);
    }

    public static class Configuration {
        // first and last stage for the given pipelines
        private final int[] extent = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE};

        private final ArrayListMultimap<Integer, Stage> stageMultimap = ArrayListMultimap.create();

        private final int initialStage;

        public Configuration(Set<Pipeline> pipelines) {
            if (pipelines.isEmpty()) {
                initialStage = extent[0] = extent[1] = 0;
                return;
            }
            pipelines.forEach(pipeline -> {
                // skip pipelines without any stages, they don't contribute any rules to run
                final SortedSet<Stage> stages = pipeline.stages();
                if (stages.isEmpty()) {
                    return;
                }
                extent[0] = Math.min(extent[0], stages.first().stage());
                extent[1] = Math.max(extent[1], stages.last().stage());
                stages.forEach(stage -> stageMultimap.put(stage.stage(), stage));
            });

            if (extent[0] == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("First stage cannot be at " + Integer.MIN_VALUE);
            }
            // the stage before the first stage.
            initialStage = extent[0] - 1;
        }

        public int initialStage() {
            return initialStage;
        }

        public int lastStage() {
            return extent[1];
        }

        public boolean hasStages(int stage) {
            return stageMultimap.containsKey(stage);
        }

        public List<Stage> getStages(int stage) {
            return stageMultimap.get(stage);
        }
    }
}
