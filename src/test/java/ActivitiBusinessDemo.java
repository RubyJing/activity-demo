import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

/**
 * Activiti进阶
 *
 * @author ZhouJing
 * @version 1.0
 * @date 2021/9/27 17:55
 */

public class ActivitiBusinessDemo {

    /**
     * 添加业务key，到Activiti的表
     */
    @Test
    public void addBusinessKey() {
        //1.获取流程引擎
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        //2.获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //3.启动流程的过程中，添加businessKey
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection", "1001");
        //4.输出
        System.out.println("businessKey=" + processInstance.getBusinessKey());
    }

    /**
     * 全部流程实例的 挂起 和 激活
     */
    @Test
    public void suspendAllProcessInstance() {
        //1.获取流程引擎
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        //2.获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("evection").singleResult();
        //4.获取当前流程定义的实例是否都是挂起状态
        boolean suspended = processDefinition.isSuspended();
        //5.获取当前流程定义id
        String processDefinitionId = processDefinition.getId();
        //6.如果是挂起状态，改为激活状态
        if (suspended) {
            repositoryService.activateProcessDefinitionById(processDefinitionId);
        } else {
            //7.如果是激活状态，改为挂起状态。
            // 参数1：流程定义id  参数2：是否暂停  参数3：暂停的时间
            repositoryService.suspendProcessDefinitionById(processDefinitionId
                    , true, null);
        }
    }

    /**
     * 挂起/激活 单个流程实例
     */
    @Test
    public void suspendSingleProcessInstance() {
        //1.获取流程引擎
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        //2.获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //3.获取流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                //act_ru_task表
                .processInstanceId("2501").singleResult();
        //4.得到当前流程实例的暂停状态，true已暂停，false激活
        boolean suspended = processInstance.isSuspended();
        if (suspended) {
            //5.如果已暂停，激活该实例
            runtimeService.activateProcessInstanceById(processInstance.getProcessInstanceId());
        } else {
            //6.如果已激活，暂停该实例
            runtimeService.suspendProcessInstanceById(processInstance.getProcessInstanceId());
        }
    }

    /**
     * 完成个人任务
     */
    @Test
    public void completeTask(){
        //1.获取流程引擎
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        //2.获取TaskService
        TaskService taskService = processEngine.getTaskService();
        //3.使用TaskService获取任务
        Task task = taskService.createTaskQuery().processInstanceId("2501")
                .taskAssignee("rose").singleResult();
        System.out.println("流程实例id=="+task.getProcessInstanceId());
        System.out.println("流程任务id=="+task.getId());
        System.out.println("负责人=="+task.getAssignee());
        System.out.println("流程实例Id=="+task.getName());
        //4.根据任务的id完成任务
        taskService.complete(task.getId());
    }
}
