/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.config.score.director;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.KieResources;
import org.kie.api.runtime.KieContainer;
import org.optaplanner.core.config.score.definition.ScoreDefinitionType;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.config.util.KeyAsElementMapConverter;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;
import org.optaplanner.core.impl.score.buildin.bendablebigdecimal.BendableBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.bendablelong.BendableLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardmediumsoft.HardMediumSoftScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoftdouble.HardSoftDoubleScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoftlong.HardSoftLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simplebigdecimal.SimpleBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simpledouble.SimpleDoubleScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simplelong.SimpleLongScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.score.director.easy.EasyScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreDirectorFactory;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XStreamAlias("scoreDirectorFactory")
public class ScoreDirectorFactoryConfig {

    private static final Logger logger = LoggerFactory.getLogger(ScoreDirectorFactoryConfig.class);

    protected Class<? extends ScoreDefinition> scoreDefinitionClass = null;
    protected ScoreDefinitionType scoreDefinitionType = null;
    protected Integer bendableHardLevelsSize = null;
    protected Integer bendableSoftLevelsSize = null;

    protected Class<? extends EasyScoreCalculator> easyScoreCalculatorClass = null;

    protected Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass = null;

    @XStreamOmitField
    protected KieBase kieBase = null;
    @XStreamImplicit(itemFieldName = "scoreDrl")
    protected List<String> scoreDrlList = null;
    @XStreamImplicit(itemFieldName = "scoreDrlFile")
    protected List<File> scoreDrlFileList = null;
    @XStreamConverter(value = KeyAsElementMapConverter.class)
    protected Map<String, String> kieBaseConfigurationProperties = null;

    protected String initializingScoreTrend = null;

    @XStreamAlias("assertionScoreDirectorFactory")
    protected ScoreDirectorFactoryConfig assertionScoreDirectorFactory = null;

    public Class<? extends ScoreDefinition> getScoreDefinitionClass() {
        return scoreDefinitionClass;
    }

    public void setScoreDefinitionClass(Class<? extends ScoreDefinition> scoreDefinitionClass) {
        this.scoreDefinitionClass = scoreDefinitionClass;
    }

    public ScoreDefinitionType getScoreDefinitionType() {
        return scoreDefinitionType;
    }

    public void setScoreDefinitionType(ScoreDefinitionType scoreDefinitionType) {
        this.scoreDefinitionType = scoreDefinitionType;
    }

    public Integer getBendableHardLevelsSize() {
        return bendableHardLevelsSize;
    }

    public void setBendableHardLevelsSize(Integer bendableHardLevelsSize) {
        this.bendableHardLevelsSize = bendableHardLevelsSize;
    }

    public Integer getBendableSoftLevelsSize() {
        return bendableSoftLevelsSize;
    }

    public void setBendableSoftLevelsSize(Integer bendableSoftLevelsSize) {
        this.bendableSoftLevelsSize = bendableSoftLevelsSize;
    }

    public Class<? extends EasyScoreCalculator> getEasyScoreCalculatorClass() {
        return easyScoreCalculatorClass;
    }

    public void setEasyScoreCalculatorClass(Class<? extends EasyScoreCalculator> easyScoreCalculatorClass) {
        this.easyScoreCalculatorClass = easyScoreCalculatorClass;
    }

    public Class<? extends IncrementalScoreCalculator> getIncrementalScoreCalculatorClass() {
        return incrementalScoreCalculatorClass;
    }

    public void setIncrementalScoreCalculatorClass(Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass) {
        this.incrementalScoreCalculatorClass = incrementalScoreCalculatorClass;
    }

    public KieBase getKieBase() {
        return kieBase;
    }

    public void setKieBase(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    public List<String> getScoreDrlList() {
        return scoreDrlList;
    }

    public void setScoreDrlList(List<String> scoreDrlList) {
        this.scoreDrlList = scoreDrlList;
    }

    public List<File> getScoreDrlFileList() {
        return scoreDrlFileList;
    }

    public void setScoreDrlFileList(List<File> scoreDrlFileList) {
        this.scoreDrlFileList = scoreDrlFileList;
    }

    public Map<String, String> getKieBaseConfigurationProperties() {
        return kieBaseConfigurationProperties;
    }

    public void setKieBaseConfigurationProperties(Map<String, String> kieBaseConfigurationProperties) {
        this.kieBaseConfigurationProperties = kieBaseConfigurationProperties;
    }

    public String getInitializingScoreTrend() {
        return initializingScoreTrend;
    }

    public void setInitializingScoreTrend(String initializingScoreTrend) {
        this.initializingScoreTrend = initializingScoreTrend;
    }

    public ScoreDirectorFactoryConfig getAssertionScoreDirectorFactory() {
        return assertionScoreDirectorFactory;
    }

    public void setAssertionScoreDirectorFactory(ScoreDirectorFactoryConfig assertionScoreDirectorFactory) {
        this.assertionScoreDirectorFactory = assertionScoreDirectorFactory;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public InnerScoreDirectorFactory buildScoreDirectorFactory(EnvironmentMode environmentMode,
            SolutionDescriptor solutionDescriptor) {
        ScoreDefinition scoreDefinition = buildScoreDefinition();
        return buildScoreDirectorFactory(environmentMode, solutionDescriptor, scoreDefinition);
    }

    protected InnerScoreDirectorFactory buildScoreDirectorFactory(EnvironmentMode environmentMode,
            SolutionDescriptor solutionDescriptor, ScoreDefinition scoreDefinition) {
        AbstractScoreDirectorFactory scoreDirectorFactory;
        // TODO this should fail-fast if multiple scoreDirectorFactory's are configured or if none are configured
        scoreDirectorFactory = buildEasyScoreDirectorFactory();
        if (scoreDirectorFactory == null) {
            scoreDirectorFactory = buildIncrementalScoreDirectorFactory();
        }
        if (scoreDirectorFactory == null) {
            scoreDirectorFactory = buildDroolsScoreDirectorFactory();
        }
        scoreDirectorFactory.setSolutionDescriptor(solutionDescriptor);
        scoreDirectorFactory.setScoreDefinition(scoreDefinition);
        if (assertionScoreDirectorFactory != null) {
            if (assertionScoreDirectorFactory.getAssertionScoreDirectorFactory() != null) {
                throw new IllegalArgumentException("A assertionScoreDirectorFactory ("
                        + assertionScoreDirectorFactory + ") cannot have a non-null assertionScoreDirectorFactory ("
                        + assertionScoreDirectorFactory.getAssertionScoreDirectorFactory() + ").");
            }
            if (assertionScoreDirectorFactory.getScoreDefinitionClass() != null
                    || assertionScoreDirectorFactory.getScoreDefinitionType() != null) {
                throw new IllegalArgumentException("A assertionScoreDirectorFactory ("
                        + assertionScoreDirectorFactory + ") must reuse the scoreDefinition of its parent." +
                        " It cannot have a non-null scoreDefinition* property.");
            }
            if (environmentMode.compareTo(EnvironmentMode.FAST_ASSERT) > 0) {
                throw new IllegalArgumentException("A non-null assertionScoreDirectorFactory ("
                        + assertionScoreDirectorFactory + ") requires an environmentMode ("
                        + environmentMode + ") of " + EnvironmentMode.FAST_ASSERT + " or lower.");
            }
            scoreDirectorFactory.setAssertionScoreDirectorFactory(
                    assertionScoreDirectorFactory.buildScoreDirectorFactory(
                            EnvironmentMode.PRODUCTION, solutionDescriptor, scoreDefinition));
        }
        scoreDirectorFactory.setInitializingScoreTrend(InitializingScoreTrend.parseTrend(
                initializingScoreTrend == null ? InitializingScoreTrendLevel.ANY.name() : initializingScoreTrend,
                scoreDefinition.getLevelsSize()));
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            scoreDirectorFactory.setAssertClonedSolution(true);
        }
        return scoreDirectorFactory;
    }

    public ScoreDefinition buildScoreDefinition() {
        if (scoreDefinitionType != ScoreDefinitionType.BENDABLE
                && scoreDefinitionType != ScoreDefinitionType.BENDABLE_LONG
                && scoreDefinitionType != ScoreDefinitionType.BENDABLE_BIG_DECIMAL
                && (bendableHardLevelsSize != null || bendableSoftLevelsSize != null)) {
            throw new IllegalArgumentException("With scoreDefinitionType (" + scoreDefinitionType
                    + ") there must be no bendableHardLevelsSize (" + bendableHardLevelsSize
                    + ") or bendableSoftLevelsSize (" + bendableSoftLevelsSize + ").");
        }
        if ((scoreDefinitionType == ScoreDefinitionType.BENDABLE
                || scoreDefinitionType == ScoreDefinitionType.BENDABLE_LONG
                || scoreDefinitionType == ScoreDefinitionType.BENDABLE_BIG_DECIMAL)
                && (bendableHardLevelsSize == null || bendableSoftLevelsSize == null)) {
            throw new IllegalArgumentException("With scoreDefinitionType (" + scoreDefinitionType
                    + ") there must be a bendableHardLevelsSize (" + bendableHardLevelsSize
                    + ") and a bendableSoftLevelsSize (" + bendableSoftLevelsSize + ").");
        }
        if (scoreDefinitionClass != null) {
            if (scoreDefinitionType != null || bendableHardLevelsSize != null || bendableSoftLevelsSize != null) {
                throw new IllegalStateException("With scoreDefinitionClass (" + scoreDefinitionClass
                        + ") there must be no scoreDefinitionType (" + scoreDefinitionType
                        + ") or bendableHardLevelsSize (" + bendableHardLevelsSize
                        + ") or bendableSoftLevelsSize (" + bendableSoftLevelsSize + ").");
            }
            return ConfigUtils.newInstance(this, "scoreDefinitionClass", scoreDefinitionClass);
        }
        if (scoreDefinitionType != null) {
            switch (scoreDefinitionType) {
                case SIMPLE:
                    return new SimpleScoreDefinition();
                case SIMPLE_LONG:
                    return new SimpleLongScoreDefinition();
                case SIMPLE_DOUBLE:
                    return new SimpleDoubleScoreDefinition();
                case SIMPLE_BIG_DECIMAL:
                    return new SimpleBigDecimalScoreDefinition();
                case HARD_SOFT:
                    return new HardSoftScoreDefinition();
                case HARD_SOFT_LONG:
                    return new HardSoftLongScoreDefinition();
                case HARD_SOFT_DOUBLE:
                    return new HardSoftDoubleScoreDefinition();
                case HARD_SOFT_BIG_DECIMAL:
                    return new HardSoftBigDecimalScoreDefinition();
                case HARD_MEDIUM_SOFT:
                    return new HardMediumSoftScoreDefinition();
                case HARD_MEDIUM_SOFT_LONG:
                    return new HardMediumSoftLongScoreDefinition();
                case BENDABLE:
                    return new BendableScoreDefinition(bendableHardLevelsSize, bendableSoftLevelsSize);
                case BENDABLE_LONG:
                    return new BendableLongScoreDefinition(bendableHardLevelsSize, bendableSoftLevelsSize);
                case BENDABLE_BIG_DECIMAL:
                    return new BendableBigDecimalScoreDefinition(bendableHardLevelsSize, bendableSoftLevelsSize);
                default:
                    throw new IllegalStateException("The scoreDefinitionType (" + scoreDefinitionType
                            + ") is not implemented.");
            }
        } else {
            return new SimpleScoreDefinition();
        }
    }

    private AbstractScoreDirectorFactory buildEasyScoreDirectorFactory() {
        if (easyScoreCalculatorClass != null) {
            EasyScoreCalculator easyScoreCalculator = ConfigUtils.newInstance(this,
                    "easyScoreCalculatorClass", easyScoreCalculatorClass);
            return new EasyScoreDirectorFactory(easyScoreCalculator);
        } else {
            return null;
        }
    }

    private AbstractScoreDirectorFactory buildIncrementalScoreDirectorFactory() {
        if (incrementalScoreCalculatorClass != null) {
            if (!IncrementalScoreCalculator.class.isAssignableFrom(incrementalScoreCalculatorClass)) {
                throw new IllegalArgumentException(
                        "The incrementalScoreCalculatorClass (" + incrementalScoreCalculatorClass
                        + ") does not implement " + IncrementalScoreCalculator.class.getSimpleName() + ".");
            }
            return new IncrementalScoreDirectorFactory(incrementalScoreCalculatorClass);
        } else {
            return null;
        }
    }

    private AbstractScoreDirectorFactory buildDroolsScoreDirectorFactory() {
        DroolsScoreDirectorFactory scoreDirectorFactory = new DroolsScoreDirectorFactory(buildKieBase());
        return scoreDirectorFactory;
    }

    private KieBase buildKieBase() {
        if (kieBase != null) {
            if (!ConfigUtils.isEmptyCollection(scoreDrlList) || !ConfigUtils.isEmptyCollection(scoreDrlFileList)) {
                throw new IllegalArgumentException("If kieBase is not null, the scoreDrlList (" + scoreDrlList
                        + ") and the scoreDrlFileList (" + scoreDrlFileList + ") must be empty.");
            }
            if (kieBaseConfigurationProperties != null) {
                throw new IllegalArgumentException("If kieBase is not null, the kieBaseConfigurationProperties ("
                        + kieBaseConfigurationProperties + ") must be null.");
            }
            return kieBase;
        } else {
            if (ConfigUtils.isEmptyCollection(scoreDrlList) && ConfigUtils.isEmptyCollection(scoreDrlFileList)) {
                throw new IllegalArgumentException("The scoreDrlList (" + scoreDrlList
                        + ") and the scoreDrlFileList (" + scoreDrlFileList + ") cannot both be empty.");
            }
            KieServices kieServices = KieServices.Factory.get();
            KieResources kieResources = kieServices.getResources();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            if (!ConfigUtils.isEmptyCollection(scoreDrlList)) {
                for (String scoreDrl : scoreDrlList) {
                    if (scoreDrl == null) {
                        throw new IllegalArgumentException("The scoreDrl (" + scoreDrl + ") cannot be null.");
                    }
                    URL scoreDrlURL = getClass().getClassLoader().getResource(scoreDrl);
                    if (scoreDrlURL == null) {
                        String errorMessage = "The scoreDrl (" + scoreDrl + ") does not exist as a classpath resource.";
                        if (scoreDrl.startsWith("/")) {
                            errorMessage += "\nAs from 6.1, a classpath resource should not start with a slash (/)."
                                    + " A scoreDrl now adheres to ClassLoader.getResource(String)."
                                    + " Remove the leading slash from the scoreDrl if you're upgrading from 6.0.";
                        }
                        throw new IllegalArgumentException(errorMessage);
                    }
                    kieFileSystem.write(kieResources.newClassPathResource(scoreDrl, "UTF-8"));
                }
            }
            if (!ConfigUtils.isEmptyCollection(scoreDrlFileList)) {
                for (File scoreDrlFile : scoreDrlFileList) {
                    if (scoreDrlFile == null) {
                        throw new IllegalArgumentException("The scoreDrlFile (" + scoreDrlFile + ") cannot be null.");
                    }
                    if (!scoreDrlFile.exists()) {
                        throw new IllegalArgumentException("The scoreDrlFile (" + scoreDrlFile
                                + ") does not exist.");
                    }
                    kieFileSystem.write(kieResources.newFileSystemResource(scoreDrlFile, "UTF-8"));
                }
            }
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            Results results = kieBuilder.getResults();
            if (results.hasMessages(Message.Level.ERROR)) {
                throw new IllegalStateException("There are errors in a score DRL:\n"
                        + results.toString());
            } else if (results.hasMessages(Message.Level.WARNING)) {
                logger.warn("There are warning in a score DRL:\n"
                        + results.toString());
            }
            KieContainer kieContainer = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());

            KieBaseConfiguration kieBaseConfiguration = kieServices.newKieBaseConfiguration();
            if (kieBaseConfigurationProperties != null) {
                for (Map.Entry<String, String> entry : kieBaseConfigurationProperties.entrySet()) {
                    kieBaseConfiguration.setProperty(entry.getKey(), entry.getValue());
                }
            }
            return kieContainer.newKieBase(kieBaseConfiguration);
        }
    }

    public void inherit(ScoreDirectorFactoryConfig inheritedConfig) {
        if (scoreDefinitionClass == null && scoreDefinitionType == null
                && bendableHardLevelsSize == null && bendableSoftLevelsSize == null) {
            scoreDefinitionClass = inheritedConfig.getScoreDefinitionClass();
            scoreDefinitionType = inheritedConfig.getScoreDefinitionType();
            bendableHardLevelsSize = inheritedConfig.getBendableHardLevelsSize();
            bendableSoftLevelsSize = inheritedConfig.getBendableSoftLevelsSize();
        }
        easyScoreCalculatorClass = ConfigUtils.inheritOverwritableProperty(
                easyScoreCalculatorClass, inheritedConfig.getEasyScoreCalculatorClass());
        incrementalScoreCalculatorClass = ConfigUtils.inheritOverwritableProperty(
                incrementalScoreCalculatorClass, inheritedConfig.getIncrementalScoreCalculatorClass());
        kieBase = ConfigUtils.inheritOverwritableProperty(
                kieBase, inheritedConfig.getKieBase());
        scoreDrlList = ConfigUtils.inheritMergeableListProperty(
                scoreDrlList, inheritedConfig.getScoreDrlList());
        scoreDrlFileList = ConfigUtils.inheritMergeableListProperty(
                scoreDrlFileList, inheritedConfig.getScoreDrlFileList());
        kieBaseConfigurationProperties = ConfigUtils.inheritMergeableMapProperty(
                kieBaseConfigurationProperties, inheritedConfig.getKieBaseConfigurationProperties());
        initializingScoreTrend = ConfigUtils.inheritOverwritableProperty(
                initializingScoreTrend, inheritedConfig.getInitializingScoreTrend());

        assertionScoreDirectorFactory = ConfigUtils.inheritOverwritableProperty(
                assertionScoreDirectorFactory, inheritedConfig.getAssertionScoreDirectorFactory());
    }

}
