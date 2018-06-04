import static org.camunda.bpm.engine.test.assertions.cmmn.CmmnAwareTests.*;

import org.camunda.bpm.engine.runtime.CaseInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

public class UnderstandingCamundaCmmnAwareTest {
    @Rule
    public ProcessEngineRule engineRule = new ProcessEngineRule();
    
    @Test
    @Deployment(resources = { "simple.cmmn" })
    public void testLoadingACmmnFile() throws Exception {
        CaseInstance instance = caseService().createCaseInstanceByKey("SimpleCase");
        assertThat(instance).isActive();
        assertThat(instance).humanTask("simplePlanItem").isActive();
    }

    @Test
    @Deployment(resources={ "simpleWithStage.cmmn" })
    public void testSimpleStageCmmnBehaviour() throws Exception {
        CaseInstance instance = caseService().createCaseInstanceByKey("SimpleWithStageCase");
        assertThat(instance)
            .isActive();
        assertThat(instance)
            .humanTask("simplePlanItem")
            .isActive();
        assertThat(instance)
            .stage("StagePlanItem")
            .isActive();
        assertThat(instance)
            .stage("StagePlanItem")
            .humanTask("StagedPlanItem")
            .isActive();
    }

    @Test
    @Deployment(resources={ "simpleWithTriggeredStage.cmmn" })
    public void testSimpleWithTriggeredStageCmmnBehaviour() throws Exception {
        CaseInstance instance = caseService().createCaseInstanceByKey("SimpleWithTriggeredStageCase");
        assertThat(instance)
            .isActive();
        assertThat(instance)
            .humanTask("simpleTriggeringPlanItem")
            .isActive();
        assertThat(instance)
            .stage("StagePlanItem")
            .isAvailable();
        complete(caseExecution("simpleTriggeringPlanItem", instance));
        assertThat(instance)
             .isActive();
        assertThat(instance)
            .stage("StagePlanItem")
            .isActive();
        assertThat(instance)
            .stage("StagePlanItem")
            .humanTask("StagedPlanItem")
            .isActive();
    }

    @Test
    @Deployment(resources={ "triggeredStageAndAdditionalPlanItem.cmmn" })
    public void testTriggeredStageAndAdditionalPlanItemCmmnBehaviour() throws Exception {
        CaseInstance instance = caseService().createCaseInstanceByKey("TriggeredStageAndAdditionalPlanItemCase");
        assertThat(instance)
            .isActive();
        assertThat(instance)
            .humanTask("simpleTriggeringPlanItem")
            .isActive();
        assertThat(instance)
            .humanTask("AnotherPlanItem")
            .isActive();
        assertThat(instance)
            .stage("StagePlanItem")
            .isAvailable();
        complete(caseExecution("simpleTriggeringPlanItem", instance));
        assertThat(instance)
             .isActive();
        assertThat(instance)
            .stage("StagePlanItem")
            .isActive();
        assertThat(instance)
            .humanTask("AnotherPlanItem")
            .isActive();
        assertThat(instance)
            .stage("StagePlanItem")
            .humanTask("StagedPlanItem")
            .isActive();
        assertThat(instance)
            .stage("StagePlanItem")
            .humanTask("TriggeredPlanItem")
            .isAvailable();
        complete(caseExecution("AnotherPlanItem", instance));
        assertThat(instance)
             .isActive();
        assertThat(instance)
            .stage("StagePlanItem")
            .isActive();
        assertThat(instance)
            .stage("StagePlanItem")
            .humanTask("StagedPlanItem")
            .isActive();
        assertThat(instance)
            .stage("StagePlanItem")
            .humanTask("TriggeredPlanItem")
            .isActive();
    }

    @Test
    @Deployment(resources={ "unexpected.cmmn" })
    public void testUnexpectedCmmnBehaviour() throws Exception {
        CaseInstance instance = caseService().createCaseInstanceByKey("UnexpectedCase");
        assertThat(instance)
            .isActive();
        assertThat(instance)
            .humanTask("TriggeringPlanItem")
            .isActive();
        assertThat(instance)
            .stage("SomeStagePlanItem")
            .isActive();
        assertThat(instance)
            .stage("SomeStagePlanItem")
            .humanTask("TriggeredPlanItem")
            .isAvailable();
        complete(caseExecution("TriggeringPlanItem", instance));
        assertThat(caseExecutionQuery().count()).isEqualTo(3L);
        assertThat(instance)
            .isActive();
        assertThat(instance)
            .stage("SomeStagePlanItem")
            .isActive();
        assertThat(instance)
            .stage("SomeStagePlanItem")
            .humanTask("TriggeredPlanItem")
            .isActive();
    }
}