import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;

/**
 * 创建activity的数据库表
 *
 * @author ZhouJing
 * @version 1.0
 * @date 2021/9/23 14:11
 */
public class CreateTable {
    public static void main(String[] args) {
        //默认方式:默认读取activiti.cfg.xml，bean的id默认是processEngineConfiguration
//        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //自定义方式
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        System.out.println("processEngine="+processEngine);


    }
}
