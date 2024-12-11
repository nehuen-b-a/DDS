package ar.edu.utn.frba.dds.domain.entities.heladeras.receptores;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.controllers.ControladorIncidenteHeladera;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public class FallaConexionJob implements Job {
  private final double tiempoMaximoInactividad = 1.5 * 60 * 1000;
  private ControladorIncidenteHeladera controladorIncidenteHeladera = ServiceLocator.instanceOf(ControladorIncidenteHeladera.class);
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    String idHeladera = jobDataMap.getString("idHeladera");
    long timestamp = jobDataMap.getLong("timestamp");
    Scheduler scheduler = context.getScheduler();

    if(System.currentTimeMillis() - timestamp > tiempoMaximoInactividad){
      this.controladorIncidenteHeladera.procesarFallaConexion(idHeladera);
      System.out.println("Se eliminará job asociado a heladera");
      JobKey jobKey = context.getJobDetail().getKey();
      try {
        scheduler.deleteJob(jobKey);
      } catch (SchedulerException e) {
        throw new RuntimeException(e);
      }
    }
  }

}
