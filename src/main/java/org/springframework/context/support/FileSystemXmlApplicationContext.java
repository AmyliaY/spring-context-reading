/*
 * Copyright 2002-2010 the original author or authors.
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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Standalone XML application context, taking the context definition files
 * from the file system or from URLs, interpreting plain paths as relative
 * file system locations (e.g. "mydir/myfile.txt"). Useful for test harnesses
 * as well as for standalone environments.
 *
 * <p><b>NOTE:</b> Plain paths will always be interpreted as relative
 * to the current VM working directory, even if they start with a slash.
 * (This is consistent with the semantics in a Servlet container.)
 * <b>Use an explicit "file:" prefix to enforce an absolute file path.</b>
 *
 * <p>The config location defaults can be overridden via {@link #getConfigLocations},
 * Config locations can either denote concrete files like "/myfiles/context.xml"
 * or Ant-style patterns like "/myfiles/*-context.xml" (see the
 * {@link org.springframework.util.AntPathMatcher} javadoc for pattern details).
 *
 * <p>Note: In case of multiple config locations, later bean definitions will
 * override ones defined in earlier loaded files. This can be leveraged to
 * deliberately override certain bean definitions via an extra XML file.
 *
 * <p><b>This is a simple, one-stop shop convenience ApplicationContext.
 * Consider using the {@link GenericApplicationContext} class in combination
 * with an {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}
 * for more flexible context setup.</b>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #getResource
 * @see #getResourceByPath
 * @see GenericApplicationContext
 */
public class FileSystemXmlApplicationContext extends AbstractXmlApplicationContext {

	public FileSystemXmlApplicationContext() {
	}

	public FileSystemXmlApplicationContext(ApplicationContext parent) {
		super(parent);
	}

	/**
	 * ������4�����췽���������˵�5�����췽��
	 * @param configLocation
	 * @throws BeansException
	 */
	
	//configLocation������BeanDefinition���ڵ��ļ�·��
	public FileSystemXmlApplicationContext(String configLocation) throws BeansException {
		this(new String[] {configLocation}, true, null);
	}

	//���Զ�����BeanDefinition���ڵ��ļ�·��
	public FileSystemXmlApplicationContext(String... configLocations) throws BeansException {
		this(configLocations, true, null);
	}

	//�ڶ�����BeanDefinition���ڵ��ļ�·�� ��ͬʱ������ָ���Լ���˫��IoC����(��������������ʲô������������get������bean��ȥ�������ң�)
	public FileSystemXmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
		this(configLocations, true, parent);
	}

	public FileSystemXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
		this(configLocations, refresh, null);
	}

	/**
	 * ���Ӧ��ֱ��ʹ��FileSystemXmlApplicationContext����ʵ�������򶼻����������췽������
	 */
	public FileSystemXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent)
			throws BeansException {

		//��̬��ȷ�����ĸ�������ȥ�������ǵ������ļ�
		super(parent);
		//���߶�ȡ�� �����ļ���������÷����̳���ү��AbstractRefreshableApplicationContext
		setConfigLocations(configLocations);
		if (refresh) {
			//������ʼ��
			refresh();
		}
	}


	/**
	 * ʵ����һ��FileSystemResource�����أ��Ա��������Դ��IO����
	 * ����������DefaultResourceLoader��getResource�����б����õģ�
	 */
	@Override
	protected Resource getResourceByPath(String path) {
		if (path != null && path.startsWith("/")) {
			path = path.substring(1);
		}
		return new FileSystemResource(path);
	}

}
