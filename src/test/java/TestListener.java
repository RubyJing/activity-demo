import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class TestListener {

    @Test
    public void testDeployment() {
        ProcessEngine defaultProcessEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .name("测试监听器")
                .addClasspathResource("bpmn/demo-listen.bpmn")
//                .addClasspathResource("bpmn/evection.png")
                .deploy();
        System.out.println("流程部署Id:" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
//        repositoryService.deleteDeployment("2501");
    }

    @Test
    public void startAssigneeUel(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        // 获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 设定负责人的值：用来替换uel表达式
        runtimeService.startProcessInstanceByKey("myProcess_1");
        
    }
}
