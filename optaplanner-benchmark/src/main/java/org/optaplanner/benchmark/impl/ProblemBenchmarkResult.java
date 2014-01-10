/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.benchmark.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.benchmark.impl.measurement.ScoreDifferencePercentage;
import org.optaplanner.benchmark.impl.ranking.SingleBenchmarkRankingComparator;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.termination.TerminationConfig;
import org.optaplanner.core.impl.solution.ProblemIO;
import org.optaplanner.core.impl.solution.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents 1 problem instance (data set) benchmarked on multiple {@link Solver} configurations.
 */
public class ProblemBenchmarkResult {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final DefaultPlannerBenchmark plannerBenchmark;

    private String name = null;

    private ProblemIO problemIO = null;
    private boolean writeOutputSolutionEnabled = false;
    private File inputSolutionFile = null;
    private File problemReportDirectory = null;

    private List<ProblemStatistic> problemStatisticList = null;

    private List<SingleBenchmarkResult> singleBenchmarkResultList = null;

    private Long problemScale = null;

    // ************************************************************************
    // Report accumulates
    // ************************************************************************

    private Long averageUsedMemoryAfterInputSolution = null;
    private Integer failureCount = null;
    private SingleBenchmarkResult winningSingleBenchmarkResult = null;
    private SingleBenchmarkResult worstSingleBenchmarkResult = null;

    public ProblemBenchmarkResult(DefaultPlannerBenchmark plannerBenchmark) {
        this.plannerBenchmark = plannerBenchmark;
    }

    public DefaultPlannerBenchmark getPlannerBenchmark() {
        return plannerBenchmark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProblemIO getProblemIO() {
        return problemIO;
    }

    public void setProblemIO(ProblemIO problemIO) {
        this.problemIO = problemIO;
    }

    public boolean isWriteOutputSolutionEnabled() {
        return writeOutputSolutionEnabled;
    }

    public void setWriteOutputSolutionEnabled(boolean writeOutputSolutionEnabled) {
        this.writeOutputSolutionEnabled = writeOutputSolutionEnabled;
    }

    public File getInputSolutionFile() {
        return inputSolutionFile;
    }

    public void setInputSolutionFile(File inputSolutionFile) {
        this.inputSolutionFile = inputSolutionFile;
    }

    public File getProblemReportDirectory() {
        return problemReportDirectory;
    }

    public List<ProblemStatistic> getProblemStatisticList() {
        return problemStatisticList;
    }

    public void setProblemStatisticList(List<ProblemStatistic> problemStatisticList) {
        this.problemStatisticList = problemStatisticList;
    }

    public List<SingleBenchmarkResult> getSingleBenchmarkResultList() {
        return singleBenchmarkResultList;
    }

    public void setSingleBenchmarkResultList(List<SingleBenchmarkResult> singleBenchmarkResultList) {
        this.singleBenchmarkResultList = singleBenchmarkResultList;
    }

    public Long getProblemScale() {
        return problemScale;
    }

    public Long getAverageUsedMemoryAfterInputSolution() {
        return averageUsedMemoryAfterInputSolution;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public SingleBenchmarkResult getWinningSingleBenchmarkResult() {
        return winningSingleBenchmarkResult;
    }

    public SingleBenchmarkResult getWorstSingleBenchmarkResult() {
        return worstSingleBenchmarkResult;
    }

    // ************************************************************************
    // Smart getters
    // ************************************************************************

    public boolean hasAnyFailure() {
        return failureCount > 0;
    }

    public boolean hasAnySuccess() {
        return singleBenchmarkResultList.size() - failureCount > 0;
    }

    public boolean hasAnyProblemStatistic() {
        return problemStatisticList.size() > 0;
    }

    // ************************************************************************
    // Work methods
    // ************************************************************************

    public void initSubdirs(File benchmarkReportDirectory) {
        problemReportDirectory = new File(benchmarkReportDirectory, name);
        problemReportDirectory.mkdirs();
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            singleBenchmarkResult.initSubdirs(problemReportDirectory);
        }
    }

    public long warmUp(long startingTimeMillis, long warmUpTimeMillisSpend, long timeLeft) {
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            SolverBenchmarkResult solverBenchmarkResult = singleBenchmarkResult.getSolverBenchmarkResult();
            TerminationConfig originalTerminationConfig = solverBenchmarkResult.getSolverConfig().getTerminationConfig();
            TerminationConfig tmpTerminationConfig = originalTerminationConfig == null
                    ? new TerminationConfig() : originalTerminationConfig.clone();
            tmpTerminationConfig.shortenMaximumTimeMillisSpendTotal(timeLeft);
            solverBenchmarkResult.getSolverConfig().setTerminationConfig(tmpTerminationConfig);

            Solver solver = solverBenchmarkResult.getSolverConfig().buildSolver();
            solver.setPlanningProblem(readPlanningProblem());
            solver.solve();

            solverBenchmarkResult.getSolverConfig().setTerminationConfig(originalTerminationConfig);
            long timeSpend = System.currentTimeMillis() - startingTimeMillis;
            timeLeft = warmUpTimeMillisSpend - timeSpend;
            if (timeLeft <= 0L) {
                return timeLeft;
            }
        }
        return timeLeft;
    }

    public Solution readPlanningProblem() {
        return problemIO.read(inputSolutionFile);
    }

    public void writeOutputSolution(SingleBenchmarkResult singleBenchmarkResult, Solution outputSolution) {
        if (!writeOutputSolutionEnabled) {
            return;
        }
        String filename = singleBenchmarkResult.getName() + "." + problemIO.getFileExtension();
        File outputSolutionFile = new File(problemReportDirectory, filename);
        problemIO.write(outputSolution, outputSolutionFile);
    }

    // ************************************************************************
    // Accumulate methods
    // ************************************************************************

    public void accumulateResults(BenchmarkReport benchmarkReport) {
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            singleBenchmarkResult.accumulateResults(benchmarkReport);
        }
        determineTotalsAndAveragesAndRanking();
        determineWinningScoreDifference();
        for (ProblemStatistic problemStatistic : problemStatisticList) {
            problemStatistic.accumulateResults(benchmarkReport);
        }
    }

    private void determineTotalsAndAveragesAndRanking() {
        failureCount = 0;
        long totalUsedMemoryAfterInputSolution = 0L;
        int usedMemoryAfterInputSolutionCount = 0;
        List<SingleBenchmarkResult> successResultList = new ArrayList<SingleBenchmarkResult>(singleBenchmarkResultList);
        // Do not rank a SingleBenchmarkResult that has a failure
        for (Iterator<SingleBenchmarkResult> it = successResultList.iterator(); it.hasNext(); ) {
            SingleBenchmarkResult singleBenchmarkResult = it.next();
            if (singleBenchmarkResult.isFailure()) {
                failureCount++;
                it.remove();
            } else {
                if (singleBenchmarkResult.getUsedMemoryAfterInputSolution() != null) {
                    totalUsedMemoryAfterInputSolution += singleBenchmarkResult.getUsedMemoryAfterInputSolution();
                    usedMemoryAfterInputSolutionCount++;
                }
            }
        }
        if (usedMemoryAfterInputSolutionCount > 0) {
            averageUsedMemoryAfterInputSolution = totalUsedMemoryAfterInputSolution
                    / (long) usedMemoryAfterInputSolutionCount;
        }
        determineRanking(successResultList);
    }

    private void determineRanking(List<SingleBenchmarkResult> rankedSingleBenchmarkResultList) {
        Comparator singleBenchmarkRankingComparator = new SingleBenchmarkRankingComparator();
        Collections.sort(rankedSingleBenchmarkResultList, Collections.reverseOrder(singleBenchmarkRankingComparator));
        int ranking = 0;
        SingleBenchmarkResult previousSingleBenchmarkResult = null;
        int previousSameRankingCount = 0;
        for (SingleBenchmarkResult singleBenchmarkResult : rankedSingleBenchmarkResultList) {
            if (previousSingleBenchmarkResult != null
                    && singleBenchmarkRankingComparator.compare(previousSingleBenchmarkResult, singleBenchmarkResult) != 0) {
                ranking += previousSameRankingCount;
                previousSameRankingCount = 0;
            }
            singleBenchmarkResult.setRanking(ranking);
            previousSingleBenchmarkResult = singleBenchmarkResult;
            previousSameRankingCount++;
        }
        winningSingleBenchmarkResult = rankedSingleBenchmarkResultList.isEmpty() ? null : rankedSingleBenchmarkResultList.get(0);
        worstSingleBenchmarkResult = rankedSingleBenchmarkResultList.isEmpty() ? null
                : rankedSingleBenchmarkResultList.get(rankedSingleBenchmarkResultList.size() - 1);
    }

    private void determineWinningScoreDifference() {
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            if (singleBenchmarkResult.isFailure()) {
                continue;
            }
            singleBenchmarkResult.setWinningScoreDifference(
                    singleBenchmarkResult.getScore().subtract(winningSingleBenchmarkResult.getScore()));
            singleBenchmarkResult.setWorstScoreDifferencePercentage(
                    ScoreDifferencePercentage.calculateScoreDifferencePercentage(
                            worstSingleBenchmarkResult.getScore(), singleBenchmarkResult.getScore()));
        }
    }

    /**
     * HACK to avoid loading the planningProblem just to extract it's problemScale.
     * Called multiple times, for every {@link SingleBenchmarkResult} of this {@link ProblemBenchmarkResult}.
     *
     * @param registeringProblemScale >= 0
     */
    public void registerProblemScale(long registeringProblemScale) {
        if (problemScale == null) {
            problemScale = registeringProblemScale;
        } else if (problemScale.longValue() != registeringProblemScale) {
            logger.warn("The problemBenchmarkResult ({}) has different problemScale values ([{},{}]).",
                    getName(), problemScale, registeringProblemScale);
            // The problemScale is not unknown (null), but known to be ambiguous
            problemScale = -1L;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ProblemBenchmarkResult) {
            ProblemBenchmarkResult other = (ProblemBenchmarkResult) o;
            return inputSolutionFile.equals(other.getInputSolutionFile());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return inputSolutionFile.hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

}