/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.sjms.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.sjms.SimpleJmsComponent;
import org.apache.camel.component.sjms.SimpleJmsComponentConfiguration;
import org.apache.camel.test.junit4.CamelTestSupport;

import org.junit.Test;

public class SimpleJmsPooledComponentTest extends CamelTestSupport {

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Test
    public void testRepeatedHelloWorld() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10); 
        final String expectedBody = "Hello World!";
        MockEndpoint mock = getMockEndpoint("mock:result");

        mock.expectedMinimumMessageCount(500);

        for (int i = 0; i < 500; i++) {
            final int tempI = i;
            Runnable worker = new Runnable() {
                
                @Override
                public void run() {
                    template.sendBody("Message " + (tempI+1) + ": " + expectedBody);                    
                }
            };
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        mock.assertIsSatisfied();

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {

                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                        "vm://broker?broker.persistent=false");
                SimpleJmsComponentConfiguration config = new SimpleJmsComponentConfiguration();
                config.setMaxConnections(3);
                config.setMaxSessions(5);
                config.setMaxConsumers(10);
                config.setMaxProducers(2);
                SimpleJmsComponent component = new SimpleJmsComponent();
                component.setConfiguration(config);
                component.setConnectionFactory(connectionFactory);
                this.getContext().addComponent("sjms", component);

                from("direct:start").to("sjms:queue:test.foo");
                from("sjms:queue:test.foo").to("mock:result");
            }
        };
    }
}
