/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.connector.unit.test.jms;

import junit.framework.Assert;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.core.axis2.Axis2SynapseEnvironment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.jms.JMSConnector;
import org.wso2.carbon.connector.jms.JMSConnectorConstants;
import org.wso2.carbon.connector.jms.JMSInitializer;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import javax.jms.Connection;

public class JMSUnitTest {
    private JMSConnector jmsConnector;
    private JMSInitializer jmsInitializer;
    private MessageContext messageContext;
    private BrokerService broker;
    private Connection connection;
    private TransportConnector connector;

    @BeforeClass
    public void setup() throws Exception {
        jmsConnector = new JMSConnector();
        jmsInitializer = new JMSInitializer();
        broker = createBroker();
        broker.start();
        ActiveMQConnectionFactory connFactory =
                new ActiveMQConnectionFactory(connector.getConnectUri()
                                                      + "?jms.prefetchPolicy.all=1");
        connection = connFactory.createConnection();
        connection.start();
        System.setProperty("carbon.home", JMSUnitTest.class.getResource("/").getFile());
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(
                MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
    }

    @Test(expectedExceptions = SynapseException.class)
    public void publishMessage() throws AxisFault, ConnectException {
        messageContext = createMessageContext();
        messageContext.setProperty(JMSConnectorConstants.DESTINATION_NAME, "JMSTest");
        messageContext.setProperty(JMSConnectorConstants.DESTINATION_TYPE, "queue1");
        messageContext.setProperty(JMSConnectorConstants.CONNECTION_FACTORY_NAME, "QueueConnectionFactory");
        messageContext.setProperty
                (JMSConnectorConstants.JAVA_NAMING_FACTORY_INITIAL, "org.apache.activemq.jndi "
                        + "ActiveMQInitialContextFactory");
        messageContext.setProperty(JMSConnectorConstants.JAVA_NAMING_PROVIDER_URL, "vm://localhost");
        messageContext.setProperty(JMSConnectorConstants.CONNECTION_POOL_SIZE, "10000");
        jmsInitializer.connect(messageContext);
        jmsConnector.connect(messageContext);
    }

    @Test
    public void publishMessage1() throws AxisFault, ConnectException {
        messageContext = createMessageContext();
        messageContext.setProperty(JMSConnectorConstants.DESTINATION_NAME, "JMSTest");
        messageContext.setProperty(JMSConnectorConstants.DESTINATION_TYPE, "queue");
        messageContext.setProperty(JMSConnectorConstants.CONNECTION_FACTORY_NAME, "QueueConnectionFactory");
        messageContext.setProperty
                (JMSConnectorConstants.JAVA_NAMING_FACTORY_INITIAL,
                 "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        messageContext.setProperty(JMSConnectorConstants.JAVA_NAMING_PROVIDER_URL, "vm://localhost");
        messageContext.setProperty(JMSConnectorConstants.CONNECTION_POOL_SIZE, "10000");
        messageContext.setProperty(JMSConnectorConstants.JMS_MESSAGE_TYPE, "ByteMessage");
        messageContext.setProperty(JMSConnectorConstants.JMS_PRIORITY, "1");
        jmsInitializer.connect(messageContext);
        jmsConnector.connect(messageContext);
        Assert.assertEquals((messageContext.getEnvelope().getBody().getFirstElement()).getText() != null,
                            true);
    }

    @Test
    public void publishMessage2() throws AxisFault, ConnectException {
        messageContext = createNewMessageContext2();
        messageContext.setProperty(JMSConnectorConstants.DESTINATION_NAME, "JMSTest");
        messageContext.setProperty(JMSConnectorConstants.DESTINATION_TYPE, "topic");
        messageContext.setProperty(JMSConnectorConstants.CONNECTION_FACTORY_NAME, "TopicConnectionFactory");
        messageContext.setProperty
                (JMSConnectorConstants.JAVA_NAMING_FACTORY_INITIAL,
                 "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        messageContext.setProperty(JMSConnectorConstants.JAVA_NAMING_PROVIDER_URL, "vm://localhost");
        messageContext.setProperty(JMSConnectorConstants.CONNECTION_POOL_SIZE, "10000");
        messageContext.setProperty(JMSConnectorConstants.DELIVERY_MODE, "1");
        messageContext.setProperty(JMSConnectorConstants.TIME_TO_LIVE, "500");
        messageContext.setProperty(JMSConnectorConstants.PRIORITY, "1");
        messageContext.setProperty(JMSConnectorConstants.USERNAME, "guest");
        messageContext.setProperty(JMSConnectorConstants.PASSWORD, "guest");
        messageContext.setProperty(JMSConnectorConstants.JMS_PRIORITY, "1");
        messageContext.setProperty(JMSConnectorConstants.JMS_TIMESTAMP, "1072017");
        messageContext.setProperty(JMSConnectorConstants.JMS_DELIVERY_MODE, "1");
        messageContext.setProperty(JMSConnectorConstants.JMS_CORRELATION_ID, "10");
        messageContext.setProperty(JMSConnectorConstants.JMS_MESSAGE_TYPE, "binary");
        messageContext.setProperty(JMSConnectorConstants.JMS_EXPIRATION, "1000");
        jmsInitializer.connect(messageContext);
        jmsConnector.connect(messageContext);
        Assert.assertEquals((messageContext.getEnvelope().getBody().getFirstElement()).getText() != null,
                            true);
    }

    @Test
    public void publishMessage3() throws AxisFault, ConnectException {
        messageContext = createNewMessageContext();
        messageContext.setProperty(JMSConnectorConstants.DESTINATION_NAME, "JMSTest");
        messageContext.setProperty(JMSConnectorConstants.DESTINATION_TYPE, "queue");
        messageContext.setProperty(JMSConnectorConstants.CONNECTION_FACTORY_NAME, "QueueConnectionFactory");
        messageContext.setProperty
                (JMSConnectorConstants.JAVA_NAMING_FACTORY_INITIAL,
                 "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        messageContext.setProperty(JMSConnectorConstants.JAVA_NAMING_PROVIDER_URL, "vm://localhost");
        messageContext.setProperty(JMSConnectorConstants.CONNECTION_POOL_SIZE, "10000");
        messageContext.setProperty(JMSConnectorConstants.DELIVERY_MODE, "1");
        messageContext.setProperty(JMSConnectorConstants.TIME_TO_LIVE, "500");
        messageContext.setProperty(JMSConnectorConstants.PRIORITY, "1");
        messageContext.setProperty(JMSConnectorConstants.USERNAME, "guest");
        messageContext.setProperty(JMSConnectorConstants.PASSWORD, "guest");
        messageContext.setProperty(JMSConnectorConstants.JMS_PRIORITY, "1");
        messageContext.setProperty(JMSConnectorConstants.JMS_TIMESTAMP, "1072017");
        messageContext.setProperty(JMSConnectorConstants.JMS_DELIVERY_MODE, "1");
        messageContext.setProperty(JMSConnectorConstants.JMS_CORRELATION_ID, "10");
        messageContext.setProperty(JMSConnectorConstants.JMS_EXPIRATION, "1000");
        jmsInitializer.connect(messageContext);
        jmsConnector.connect(messageContext);
        Assert.assertEquals((messageContext.getEnvelope().getBody().getFirstElement()).getText() != null,
                            true);
    }

    @Test
    public void publishMessage4() throws AxisFault, ConnectException {
        messageContext = createNewMessageContext1();
        messageContext.setProperty(JMSConnectorConstants.DESTINATION_NAME, "JMSTest");
        messageContext.setProperty(JMSConnectorConstants.DESTINATION_TYPE, "queue");
        messageContext.setProperty(JMSConnectorConstants.CONNECTION_FACTORY_NAME, "QueueConnectionFactory");
        messageContext.setProperty
                (JMSConnectorConstants.JAVA_NAMING_FACTORY_INITIAL,
                 "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        messageContext.setProperty(JMSConnectorConstants.JAVA_NAMING_PROVIDER_URL, "vm://localhost");
        messageContext.setProperty(JMSConnectorConstants.CONNECTION_POOL_SIZE, "10000");
        messageContext.setProperty(JMSConnectorConstants.DELIVERY_MODE, "1");
        messageContext.setProperty(JMSConnectorConstants.TIME_TO_LIVE, "500");
        messageContext.setProperty(JMSConnectorConstants.PRIORITY, "1");
        messageContext.setProperty(JMSConnectorConstants.USERNAME, "guest");
        messageContext.setProperty(JMSConnectorConstants.PASSWORD, "guest");
        messageContext.setProperty(JMSConnectorConstants.JMS_PRIORITY, "1");
        messageContext.setProperty(JMSConnectorConstants.JMS_TIMESTAMP, "1072017");
        messageContext.setProperty(JMSConnectorConstants.JMS_DELIVERY_MODE, "1");
        messageContext.setProperty(JMSConnectorConstants.JMS_CORRELATION_ID, "10");
        messageContext.setProperty(JMSConnectorConstants.JMS_MESSAGE_TYPE, "binary");
        messageContext.setProperty(JMSConnectorConstants.JMS_EXPIRATION, "1000");
        jmsInitializer.connect(messageContext);
        jmsConnector.connect(messageContext);
        Assert.assertEquals((messageContext.getEnvelope().getBody().getFirstElement()).getText() != null,
                            true);
    }

    @Test
    public void publishMessage5() throws AxisFault, ConnectException {
        messageContext = createMessageContext();
        messageContext.setProperty(JMSConnectorConstants.DESTINATION_NAME, "JMSTest");
        messageContext.setProperty(JMSConnectorConstants.DESTINATION_TYPE, "topic");
        messageContext.setProperty(JMSConnectorConstants.CONNECTION_FACTORY_NAME, "TopicConnectionFactory");
        messageContext.setProperty
                (JMSConnectorConstants.JAVA_NAMING_FACTORY_INITIAL,
                 "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        messageContext.setProperty(JMSConnectorConstants.JAVA_NAMING_PROVIDER_URL, "vm://localhost");
        messageContext.setProperty(JMSConnectorConstants.CONNECTION_POOL_SIZE, "10000");
        ((Axis2MessageContext)messageContext).getAxis2MessageContext().setProperty
                (JMSConnectorConstants.JMS_MESSAGE_TYPE, "ByteMessage");
        jmsInitializer.connect(messageContext);
        jmsConnector.connect(messageContext);
        Assert.assertEquals((messageContext.getEnvelope().getBody().getFirstElement()).getText() != null,
                            true);
    }

    protected MessageContext createMessageContext() throws AxisFault {
        org.apache.axis2.context.MessageContext mc = new org.apache.axis2.context.MessageContext();
        SynapseConfiguration config = new SynapseConfiguration();
        AxisConfiguration axisConfiguration = new AxisConfiguration();
        ConfigurationContext context = new ConfigurationContext(axisConfiguration);
        mc.setConfigurationContext(context);
        SynapseEnvironment env = new Axis2SynapseEnvironment(config);
        MessageContext messageContext = new Axis2MessageContext(mc, config, env);
        org.apache.axiom.soap.SOAPEnvelope envelope = OMAbstractFactory.getSOAP11Factory().getDefaultEnvelope();
        OMDocument omDoc = OMAbstractFactory.getSOAP11Factory().createOMDocument();
        OMFactory omFactory = envelope.getOMFactory();
        omDoc.addChild(envelope);
        OMElement newElement = omFactory.createOMElement("binary", null);
        envelope.getBody().addChild(newElement);
        messageContext.setEnvelope(envelope);
        return messageContext;
    }

    protected MessageContext createNewMessageContext() throws AxisFault {
        org.apache.axis2.context.MessageContext mc = new org.apache.axis2.context.MessageContext();
        SynapseConfiguration config = new SynapseConfiguration();
        SynapseEnvironment env = new Axis2SynapseEnvironment(config);
        MessageContext messageContext = new Axis2MessageContext(mc, config, env);
        org.apache.axiom.soap.SOAPEnvelope envelope = OMAbstractFactory.getSOAP11Factory().getDefaultEnvelope();
        OMDocument omDoc = OMAbstractFactory.getSOAP11Factory().createOMDocument();
        OMFactory omFactory = envelope.getOMFactory();
        omDoc.addChild(envelope);
        OMElement newElement = omFactory.createOMElement(JMSConnectorConstants.DEFAULT_BINARY_WRAPPER);
        envelope.getBody().addChild(newElement);
        messageContext.setEnvelope(envelope);
        return messageContext;
    }

    protected MessageContext createNewMessageContext1() throws AxisFault {
        org.apache.axis2.context.MessageContext mc = new org.apache.axis2.context.MessageContext();
        SynapseConfiguration config = new SynapseConfiguration();
        SynapseEnvironment env = new Axis2SynapseEnvironment(config);
        MessageContext messageContext = new Axis2MessageContext(mc, config, env);
        org.apache.axiom.soap.SOAPEnvelope envelope = OMAbstractFactory.getSOAP11Factory().getDefaultEnvelope();
        OMDocument omDoc = OMAbstractFactory.getSOAP11Factory().createOMDocument();
        OMFactory omFactory = envelope.getOMFactory();
        omDoc.addChild(envelope);
        OMElement newElement = omFactory.createOMElement(JMSConnectorConstants.DEFAULT_TEXT_WRAPPER);
        envelope.getBody().addChild(newElement);
        messageContext.setEnvelope(envelope);
        return messageContext;
    }

    protected MessageContext createNewMessageContext2() throws AxisFault {
        org.apache.axis2.context.MessageContext mc = new org.apache.axis2.context.MessageContext();
        SynapseConfiguration config = new SynapseConfiguration();
        SynapseEnvironment env = new Axis2SynapseEnvironment(config);
        MessageContext messageContext = new Axis2MessageContext(mc, config, env);
        org.apache.axiom.soap.SOAPEnvelope envelope = OMAbstractFactory.getSOAP11Factory().getDefaultEnvelope();
        OMDocument omDoc = OMAbstractFactory.getSOAP11Factory().createOMDocument();
        OMFactory omFactory = envelope.getOMFactory();
        omDoc.addChild(envelope);
        OMElement newElement = omFactory.createOMElement(JMSConnectorConstants.MAP_QNAME);
        envelope.getBody().addChild(newElement);
        messageContext.setEnvelope(envelope);
        return messageContext;
    }

    private BrokerService createBroker() throws Exception {
        BrokerService service = new BrokerService();
        service.setPersistent(false);
        service.setUseJmx(false);
        connector = service.addConnector("tcp://localhost:61616");
        return service;
    }

    @AfterClass
    public void close() throws Exception {
        connection.close();
        broker.stop();
        PrivilegedCarbonContext.endTenantFlow();
    }
}
