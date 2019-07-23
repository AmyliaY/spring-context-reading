/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.support;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

/**
 * Convenient base class for {@link org.springframework.context.ApplicationContext}
 * implementations, drawing configuration from XML documents containing bean definitions
 * understood by an {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}.
 *
 * <p>Subclasses just have to implement the {@link #getConfigResources} and/or
 * the {@link #getConfigLocations} method. Furthermore, they might override
 * the {@link #getResourceByPath} hook to interpret relative paths in an
 * environment-specific fashion, and/or {@link #getResourcePatternResolver}
 * for extended pattern resolution.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #getConfigResources
 * @see #getConfigLocations
 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {

	private boolean validating = true;


	/**
	 * Create a new AbstractXmlApplicationContext with no parent.
	 */
	public AbstractXmlApplicationContext() {
	}

	/**
	 * Create a new AbstractXmlApplicationContext with the given parent context.
	 * @param parent the parent context
	 */
	public AbstractXmlApplicationContext(ApplicationContext parent) {
		super(parent);
	}


	/**
	 * Set whether to use XML validation. Default is {@code true}.
	 */
	public void setValidating(boolean validating) {
		this.validating = validating;
	}

	/*
	 * ʵ����ү��AbstractRefreshableApplicationContext�ĳ��󷽷�
	 */
	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
		//DefaultListableBeanFactoryʵ����BeanDefinitionRegistry�ӿڣ��ڳ�ʼ��XmlBeanDefinitionReaderʱ
		//��BeanDefinitionע����ע���BeanDefinition��ȡ��
		//���� ���ڴ�Xml�ж�ȡBeanDefinition�Ķ�ȡ������ͨ���ص����õ�IoC������ȥ������ʹ�øö�ȡ����ȡBeanDefinition��Դ
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

		beanDefinitionReader.setEnvironment(this.getEnvironment());
		//ΪbeanDefinition��ȡ������ ��Դ�����������ڱ����̫үү��AbstractApplicationContext
		//�̳���DefaultResourceLoader����ˣ�����������Ҳ��һ����Դ������
		beanDefinitionReader.setResourceLoader(this);
		//����SAX��������SAX��simple API for XML������һ��XML���������������DOM��SAX�ٶȸ��죬ռ���ڴ��С��
		//������ɨ���ĵ���һ��ɨ��һ�߽�����������Ƚ�����XML�ļ�ɨ����ڴ棬�ٽ��н�����DOM��SAX�����ڽ����ĵ�������ʱ��ֹͣ������������Ҳ��DOM���ӡ�
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

		//��ʼ��beanDefinition��ȡ�����÷���ͬʱ������Xml��У�����
		initBeanDefinitionReader(beanDefinitionReader);
		//Bean��ȡ������ʵ�ּ��صķ���
		loadBeanDefinitions(beanDefinitionReader);
	}

	/**
	 * Initialize the bean definition reader used for loading the bean
	 * definitions of this context. Default implementation is empty.
	 * <p>Can be overridden in subclasses, e.g. for turning off XML validation
	 * or using a different XmlBeanDefinitionParser implementation.
	 * @param reader the bean definition reader used by this context
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader#setDocumentReaderClass
	 */
	protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
		reader.setValidating(this.validating);
	}

	//�ô�������XmlBeanDefinitionReader��ȡ������Xml�ļ��е�BeanDefinition
	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
		
		/**
		 * ClassPathXmlApplicationContext��FileSystemXmlApplicationContext
		 * ������ĵ��ó��ַ��磬���԰���ͬ�ķ�ʽ���ؽ���Resource��Դ
		 * ����ھ���Ľ�����BeanDefinition��λ���ֻ���;ͬ��
		 */
		//��ȡ�����BeanDefinition������Resource��
		//FileSystemXmlApplicationContext��δ��getConfigResources()�������£�
		//���Ե��ø���ģ�return null��
		//��ClassPathXmlApplicationContext�Ը÷�����������д���������õ�ֵ
		Resource[] configResources = getConfigResources();
		if (configResources != null) {
			//Xml Bean��ȡ�������丸��AbstractBeanDefinitionReader��ȡ��λ��Bean������Դ
			reader.loadBeanDefinitions(configResources);
		}
		//���ø���AbstractRefreshableConfigApplicationContextʵ�ֵķ���ֵΪString[]��getConfigLocations()������
		//���ȷ���FileSystemXmlApplicationContext���췽���е���setConfigLocations()�������õ���Դ
		String[] configLocations = getConfigLocations();
		if (configLocations != null) {
			//XmlBeanDefinitionReader��ȡ�������丸��AbstractBeanDefinitionReader�ķ���������λ�ü���BeanDefinition
			reader.loadBeanDefinitions(configLocations);
		}
	}

	/**
	 * Return an array of Resource objects, referring to the XML bean definition
	 * files that this context should be built with.
	 * <p>The default implementation returns {@code null}. Subclasses can override
	 * this to provide pre-built Resource objects rather than location Strings.
	 * @return an array of Resource objects, or {@code null} if none
	 * @see #getConfigLocations()
	 */
	protected Resource[] getConfigResources() {
		return null;
	}

}
