import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * TODO:类描述
 *
 * @author ZhouJing
 * @version 1.0
 * @date 2021/9/25 21:51
 */
public class ActivitiDemo {

    @Test
    public void testDeployment() {
        ProcessEngine defaultProcessEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .name("出差申请流程")
                .addClasspathResource("bpmn/evection.bpmn20.xml")
                .addClasspathResource("bpmn/evection.png")
                .deploy();
        System.out.println("流程部署Id:" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
//        repositoryService.deleteDeployment("2501");
    }

    @Test
    public void testStartProcess() {
        ProcessEngine defaultProcessEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection");
        System.out.println("流程定义Id:" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例Id:" + processInstance.getProcessInstanceId());
        System.out.println("流程活动Id:" + processInstance.getActivityId());

    }

    /**
     * 查询个人待执行的任务
     */
    @Test
    public void testFindPersonalTaskList() {
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        // 根据流程的Key 和 任务的负责人 查询任务
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("evection")
                .taskAssignee("张三").list();

        for (Task task : taskList) {
            System.out.println("流程实例Id=" + task.getProcessInstanceId());
            System.out.println("任务Id=" + task.getId());
            System.out.println("任务负责人=" + task.getAssignee());
            System.out.println("任务的名称=" + task.getName());
        }
    }

    /**
     * 完成个人任务
     */
    @Test
    public void completeTask() {
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();

//        //完成张三的任务
//        taskService.complete("2505");

        //获取jerry - evection对应的任务
        Task task = taskService.createTaskQuery().processDefinitionKey("evection").taskAssignee("张三").singleResult();
        taskService.complete(task.getId());
    }

    /**
     * 使用zip包进行批量的部署
     */
    @Test
    public void deployProcessByZip() {
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 流程部署
        //读取资源包文件，构造成inputStream
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("bpmn/evection.zip");
        // 用inputStream 构造 zipInputStream
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 使用压缩包的流进行流程处理
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream).deploy();
        System.out.println("流程部署id=" + deployment.getId());
        System.out.println("流程部署的名称=" + deployment.getName());
    }

    /**
     * 查询流程定义
     */
    @Test
    public void queryProcessDefinition() {
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> evectionList = definitionQuery.processDefinitionKey("evection").orderByProcessDefinitionVersion()
                .desc().list();
        for (ProcessDefinition definition : evectionList) {
            System.out.println("流程定义ID：" + definition.getId());
            System.out.println("流程定义名称：" + definition.getName());
            System.out.println("流程定义key：" + definition.getKey());
            System.out.println("流程定义版本：" + definition.getVersion());
            System.out.println("流程部署id：" + definition.getDeploymentId());
        }
    }

    /**
     * 删除流程部署信息
     * act_ge_bytearray
     * act_re_deployment
     * act_re_procdef
     *
     * 当前的流程如果并没有完成，想要删除的话需要使用特殊方式。原理就是 级联删除
     *
     */
    @Test
    public void deleteDeployMent() {
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //通过部署id来删除：有任务执行时，删除会报错
//        repositoryService.deleteDeployment("1");

        // 执行级联删除
        repositoryService.deleteDeployment("1",true);
    }

    /**
     * 下载 资源文件
     * 方案1：使用Activiti提供的api，来下载资源文件
     * 方案2：自己写代码从数据库中下载文件（需了解数据结构），使用jdbc对blob/clob类型读取出来，保存到文件目录
     * 解决IO操作：Commons-io.jar
     *
     * 优先使用方案1：RepositoryService
     */
    @Test
    public void getDeployment() throws IOException {
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("evection")
                .singleResult();
        // 拿到部署id
        String deploymentId = processDefinition.getDeploymentId();
        // 从流程定义表中，获取png图片的目录和名字
        String pngName = processDefinition.getDiagramResourceName();
        // 获取图片的资源
        InputStream pngInput = repositoryService.getResourceAsStream(deploymentId, pngName);
        // 获取bpmn文件，同上
        String resourceName = processDefinition.getResourceName();
        InputStream bpmnInput = repositoryService.getResourceAsStream(deploymentId, resourceName);
        //构造OutputStream流
        File pngFile = new File("F:/下载/evectionflow01.png");
        File bpmnFile = new File("F:/下载/evectionflow01.bpmn20.xml");
        FileOutputStream pngOutStream = new FileOutputStream(pngFile);
        FileOutputStream bpmnOutStream = new FileOutputStream(bpmnFile);
        // 输入流、输出流转换
        IOUtils.copy(pngInput,pngOutStream);
        IOUtils.copy(bpmnInput,bpmnOutStream);
        // 关闭流
        pngOutStream.close();
        bpmnOutStream.close();
        pngInput.close();
        bpmnInput.close();
    }

    /**
     * 查看历史信息
     */
    @Test
    public void findHistoryInfo(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        // 获取 actinst 表的查询对象
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();
        historicActivityInstanceQuery.processInstanceId("2501").orderByActivityId().asc();
        // 查询所有内容
        List<HistoricActivityInstance> instanceList = historicActivityInstanceQuery.list();
        for (HistoricActivityInstance activityInstance : instanceList) {
            System.out.println(activityInstance.getAssignee());
            System.out.println(activityInstance.getProcessInstanceId());
            System.out.println(activityInstance.getActivityName());
            System.out.println(activityInstance.getActivityId());
            System.out.println("=================");
        }
    }


}
