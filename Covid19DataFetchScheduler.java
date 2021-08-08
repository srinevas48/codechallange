package com.codechallenge.core.schedulers;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codechallenge.core.services.Covid19DataFetchService;

@Designate(ocd = Covid19DataFetchScheduler.Config.class)
@Component(service = Runnable.class)
public class Covid19DataFetchScheduler implements Runnable {

	@ObjectClassDefinition(name = "A Scheduler to fetch COVID 19 Data for Germany", description = "This will fetch Covid 19 Data For Germany Country")
	public static @interface Config {
		/** "0 * * * * ?" **/
		@AttributeDefinition(name = "Cron-job expression")
		String scheduler_expression() default "0 0 12 1/1 * ? *";

		@AttributeDefinition(name = "Concurrent task", description = "Whether or not to schedule this task concurrently")
		boolean scheduler_concurrent() default false;

		@AttributeDefinition(name = "Covid 19 Data End Point", description = "Covid 19 Data End Point to fetch Data")
		String dataEndPoint() default "https://api.covid19api.com/live/country/germany";

		@AttributeDefinition(name = "Path to save or update Covid 19 Data", description = "A path to save or update Covid 19 data Fecthed")
		String covid19DataPath() default "/content/codechallenge/covid19";
	}

	@Reference
	Covid19DataFetchService dataFetchService;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private String dataEndPoint;
	private String covid19DataPath;

	@Override
	public void run() {
		logger.debug("Covid19DataFetchScheduler is now running, dataEndPoint='{}'", dataEndPoint);
		dataFetchService.fetchAndStoreCovid19Data(covid19DataPath, dataEndPoint);
	}

	@Activate
	protected void activate(final Config config) {
		dataEndPoint = config.dataEndPoint();
		covid19DataPath = config.covid19DataPath();
	}

}
