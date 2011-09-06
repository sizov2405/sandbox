/**
 * 
 */
package org.apache.camel.ds.routebuilder.service.provider;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sully6768
 * 
 */
@Component(enabled = true, immediate = true)
@Service(
        value = {RouteBuilder.class})
public class RouteBuilderProvider extends RouteBuilder {

	private static final transient Logger LOG = LoggerFactory
			.getLogger(RouteBuilderProvider.class);

    @Override
	public void configure() throws Exception {
		LOG.info("Configuring the DemoRouteBuilderProvider");
		from("seda:start").process(new Processor() {

			public void process(Exchange arg0) throws Exception {
				String in = (String) arg0.getIn().getBody();
				arg0.getOut().setBody(new SayHiBean().sayHi(in));
			}
		}).to("seda:next");

		from("seda:next").to("log:route1?level=INFO");
	}

}
