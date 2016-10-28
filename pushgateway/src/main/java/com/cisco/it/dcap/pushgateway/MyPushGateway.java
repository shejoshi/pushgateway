package com.cisco.it.dcap.pushgateway;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;

public class MyPushGateway {
	
	void executeBatchJob(Boolean exception) throws Exception {
		 CollectorRegistry registry = new CollectorRegistry();
		 Gauge duration = Gauge.build()
		     .name("test_pushgateway_job_duration_seconds")
		     .help("Duration of my batch job in seconds.")
		     .register(registry);
		 Gauge.Timer durationTimer = duration.startTimer();
		 try {
		   
		    System.out.println("I am in executing my job" + exception);
		    Thread.sleep(30000);
		    if (exception) {
		    	throw new Exception("Foreful exception");
		    }
		    
		    System.out.println("I done executing");
		   // This is only added to the registry after success,
		   // so that a previous success in the Pushgateway is not overwritten on failure.
		   Gauge lastSuccess = Gauge.build()
		       .name("test_pushgateway_job_last_success_unixtime")
		       .help("Last time my batch job succeeded, in unixtime.")
		       .register(registry);
		   lastSuccess.setToCurrentTime();
		 } catch (Exception e) {
			 System.out.println("I am in exception block");
			 Gauge lastFailure = Gauge.build()
				       .name("test_pushgateway_job_last_failure_unixtime")
				       .help("Last time my batch job failed, in unixtime.")
				       .register(registry);
			 lastFailure.setToCurrentTime();
			 
		 } finally {
		   durationTimer.setDuration();
		   PushGateway pg = new PushGateway("173.36.81.69:9091");
		   pg.pushAdd(registry, "test_pushgateway_job");
		 }
		}
	
	public static void main(String[] args) {
		MyPushGateway pushJob = new MyPushGateway();
		System.out.println("Starting Job");
		try {
			pushJob.executeBatchJob(Boolean.valueOf(args[0]));
		} catch (Exception e) {
			System.out.println("Job failure");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



}
