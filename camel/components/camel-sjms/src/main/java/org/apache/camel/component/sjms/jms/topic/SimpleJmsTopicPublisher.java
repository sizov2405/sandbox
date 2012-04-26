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
package org.apache.camel.component.sjms.jms.topic;

import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TopicPublisher;

import org.apache.camel.Exchange;
import org.apache.camel.component.sjms.SimpleJmsEndpoint;
import org.apache.camel.component.sjms.SimpleJmsProducer;
import org.apache.camel.component.sjms.pool.TopicProducerPool;

/**
 *
 */
public class SimpleJmsTopicPublisher extends SimpleJmsProducer {
    private TopicProducerPool producers;

    public SimpleJmsTopicPublisher(SimpleJmsEndpoint endpoint) {
        super(endpoint);
    }

    public void process(Exchange exchange) throws Exception {
        TextMessage textMessage = createTextMessage();
        textMessage.setText((String) exchange.getIn().getBody());
        TopicPublisher sender = producers.borrowObject();
        producers.returnObject(sender);
        sender.send(textMessage);
        logger.info((String) exchange.getIn().getBody());
    }
    
    private TextMessage createTextMessage() throws Exception {
        Session s = getSimpleJmsEndpoint().getSessions().borrowObject();
        TextMessage textMessage = s.createTextMessage();
        getSimpleJmsEndpoint().getSessions().returnObject(s);
        return textMessage;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        producers = new TopicProducerPool(
                getSimpleJmsEndpoint().getConfiguration().getMaxProducers(), 
                getSimpleJmsEndpoint().getSessions(), 
                getSimpleJmsEndpoint().getDestinationName(), 
                null);
        producers.fillPool();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        producers.drainPool();
        producers = null;
    }

}