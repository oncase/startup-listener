package pt.webdetails.cpk;

import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.parameters.DuplicateParamException;
import org.pentaho.di.core.parameters.NamedParams;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.platform.api.engine.IApplicationContext;
import org.pentaho.platform.api.engine.ILogoutListener;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

public class SessionListener
  implements ApplicationListener, ILogoutListener
{
  private static final Log logger = LogFactory.getLog(SessionListener.class);
  
  private static final String PLUGIN_DIR = "system/startupRuleEngine/endpoints/kettle/";
  
  private static final String LOGIN_JOB = "_executeAtLogin.kjb";
  
  private static final String LOGOUT_JOB = "_executeAtLogout.kjb";
  private static final String CPK_SOLUTION_SYSTEM_DIR = "cpk.solution.system.dir";
  private static final String CPK_WEBAPP_DIR = "cpk.webapp.dir";
  private static final String CPK_SESSION_USERNAME = "cpk.session.username";
  
  public SessionListener()
  {
    PentahoSystem.addLogoutListener(this);
    logger.debug("Created session login/logout listeners");
  }
  
  private void setParameter(NamedParams params, String paramName, String value) {
    try {
      params.addParameterDefinition(paramName, value, null);
    } catch (DuplicateParamException ex) {
      try {
        params.setParameterValue(paramName, value);
      }
      catch (UnknownParamException ex1) {}
    }
  }
  
  private String getSolutionSystemDir()
  {
    return PentahoSystem.getApplicationContext().getSolutionRootPath() + File.separator + "system";
  }
  
  private String getWebAppDir() {
    return PentahoSystem.getApplicationContext().getApplicationPath("");
  }
  
  private String getSessionUserName() {
    IPentahoSession session = PentahoSessionHolder.getSession();
    if (session != null) {
      return session.getName();
    }
    return null;
  }
  
  private void executeJob(String kettleJobPath) {
    logger.info("Executing job '" + kettleJobPath + "'");
    
    try
    {
      JobMeta jobMeta = new JobMeta(kettleJobPath, null);
      

      setParameter(jobMeta, "cpk.solution.system.dir", getSolutionSystemDir());
      setParameter(jobMeta, "cpk.webapp.dir", getWebAppDir());
      setParameter(jobMeta, "cpk.session.username", getSessionUserName());
      

      Job job = new Job(null, jobMeta);
      

      job.start();
      job.waitUntilFinished();
    } catch (Exception e) {
      logger.error("Failed to execute job '" + kettleJobPath + "'", e);
    }
  }
  
  public void onApplicationEvent(ApplicationEvent event)
  {
    if ((event instanceof AuthenticationSuccessEvent)) {
      logger.debug("Login detected");
      executeJob(PentahoSystem.getApplicationContext().getSolutionRootPath() + File.separator + "system/startupRuleEngine/endpoints/kettle/" + "_executeAtLogin.kjb");
    }
  }
  
  public void onLogout(IPentahoSession iPentahoSession) {
    logger.debug("Logout detected");
    executeJob(PentahoSystem.getApplicationContext().getSolutionRootPath() + File.separator + "system/startupRuleEngine/endpoints/kettle/" + "_executeAtLogout.kjb");
  }
}