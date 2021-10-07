import com.zj.demo.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestCandidate {

    @Test
    public void testDeployment() {
        ProcessEngine defaultProcessEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .name("出差申请流程-candidate")
                .addClasspathResource("bpmn/evection-candidate.bpmn")
//                .addClasspathResource("bpmn/evection.png")
                .deploy();
        System.out.println("流程部署Id:" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
//        repositoryService.deleteDeployment("2501");
    }

    @Test
    public void testStartProcess(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        // 获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();

        String key = "evection-candidate";
        // 设定负责人的值：用来替换uel表达式
        runtimeService.startProcessInstanceByKey(key);
        
    }

    @Test
    public void findGroupTaskList(){
        String key = "evection-candidate";

        //任务候选人
        String candidateUser = "wangwu";
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();

        //查询组任务
        List<Task> taskList = taskService.createTaskQuery().processDefinitionKey(key)
//                .taskCandidateUser(candidateUser)
                .list();
        for (Task task : taskList) {
            System.out.println("==============");
            System.out.println("流程实例ID="+task.getProcessInstanceId());
            System.out.println("任务ID="+task.getId());
            System.out.println("任务负责人="+task.getAssignee());

        }
    }

    @Test
    public void claimTask(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();

        //要拾取的任务id
        String taskId = "77505";
        // 任务候选人Id
        String userId = "wangwu";

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskCandidateUser(userId)
                .singleResult();

        if (task!=null){
            //拾取组任务
            taskService.claim(taskId,userId);
            System.out.println("任务拾取成功");
        }
    }

    /**
     * 任务的归还
     */
    @Test
    public void testAssigneeToGroupTask(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //当前的任务id
        String taskId = "77505";
        // 任务候选人Id
        String assignee = "wangwu";
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(assignee)
                .singleResult();

        if (task!=null){
            //归还 组任务:就是把负责人设置为null
            taskService.setAssignee(taskId,null);
            System.out.println("任务拾取成功");
        }
    }

    @Test
    public void complete(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();

        //完成流程时设置流程变量map
        Map<String,Object> variables = new HashMap<>();
        Evection evection = new Evection();
        evection.setDay(4d);
        variables.put("evection",evection);

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processDefinitionKey("evection-candidate").singleResult();
        taskService.complete(task.getId(),variables);
    }

    @Test
    public void cascadeDelete(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.deleteDeployment("67501",true);
    }
}
