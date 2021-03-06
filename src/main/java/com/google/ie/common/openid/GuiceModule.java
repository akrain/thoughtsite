/* Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS.
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.google.ie.common.openid;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.ie.common.openid.appengine.AppEngineHttpFetcher;
import com.google.ie.common.openid.appengine.AppEngineTrustsRootProvider;
import com.google.ie.common.openid.appengine.Openid4javaFetcher;
import com.google.ie.common.openid.appengine.SerialExecutorService;
import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.step2.consumer.OAuthProviderInfoStore;
import com.google.step2.discovery.DefaultHostMetaFetcher;
import com.google.step2.discovery.HostMetaFetcher;
import com.google.step2.discovery.ParallelHostMetaFetcher;
import com.google.step2.http.HttpFetcher;
import com.google.step2.hybrid.HybridOauthMessage;
import com.google.step2.openid.ax2.AxMessage2;
import com.google.step2.xmlsimplesign.CertValidator;
import com.google.step2.xmlsimplesign.CnConstraintCertValidator;
import com.google.step2.xmlsimplesign.DefaultCertValidator;
import com.google.step2.xmlsimplesign.DisjunctiveCertValidator;
import com.google.step2.xmlsimplesign.TrustRootsProvider;

import org.openid4java.consumer.ConsumerAssociationStore;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.message.Message;
import org.openid4java.message.MessageException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @author Dirk Balfanz (dirk.balfanz@gmail.com)
 * @author Breno de Medeiros (breno.demedeiros@gmail.com)
 */
public class GuiceModule extends AbstractModule {
    @Override
    protected void configure() {

        try {
            Message.addExtensionFactory(AxMessage2.class);
        } catch (MessageException e) {
            throw new CreationException(null);
        }

        try {
            Message.addExtensionFactory(HybridOauthMessage.class);
        } catch (MessageException e) {
            throw new CreationException(null);
        }

        bind(ConsumerAssociationStore.class)
                        .to(InMemoryConsumerAssociationStore.class)
                        .in(Scopes.SINGLETON);

        bind(OAuthProviderInfoStore.class)
                        .to(SimpleProviderInfoStore.class).in(Scopes.SINGLETON);

        if (isRunningOnAppengine()) {
            install(new AppEngineModule());
        } else {
            install(new JettyModule());
        }
    }

    private boolean isRunningOnAppengine() {

        if (System.getSecurityManager() == null) {
            return false;
        }

        return System.getSecurityManager().getClass().getCanonicalName()
                        .startsWith("com.google");
    }

    // we're using a cert validator that will validate certs either if they
    // belong to the expected signer of the XRD, or if they're signed
    // by Google.
    @Provides
    @Singleton
    public CertValidator provideCertValidator(DefaultCertValidator defaultValidator) {
        CertValidator hardCodedValidator = new CnConstraintCertValidator() {
            @Override
            protected String getRequiredCn(String authority) {
                return "hosted-id.google.com";
            }
        };

        return new DisjunctiveCertValidator(defaultValidator, hardCodedValidator);
    }

    // we're using a ParallelHostMetaFetcher to fetch host-metas both from their
    // default location, and from a special location at Google.
    @Provides
    @Singleton
    public HostMetaFetcher provideHostMetaFetcher(
                    @Named("HostMetaFetcherExecutor") ExecutorService executor,
                    DefaultHostMetaFetcher fetcher1,
                    GoogleHostedHostMetaFetcher fetcher2) {

        // we're waiting at most 10 seconds for the two host-meta fetchers to
        // find
        // a host-meta
        long hostMetatimeout = 10; // seconds.

        return new ParallelHostMetaFetcher(executor, hostMetatimeout,
                        fetcher1, fetcher2);
    }

    public static class JettyModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ExecutorService.class)
                            .annotatedWith(Names.named("HostMetaFetcherExecutor"))
                            .toInstance(Executors.newFixedThreadPool(20));
        }
    }

    public static class AppEngineModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(HttpFetcher.class)
                            .to(AppEngineHttpFetcher.class).in(Scopes.SINGLETON);

            bind(TrustRootsProvider.class)
                            .to(AppEngineTrustsRootProvider.class).in(Scopes.SINGLETON);

            bind(ExecutorService.class)
                            .annotatedWith(Names.named("HostMetaFetcherExecutor"))
                            .to(SerialExecutorService.class).in(Scopes.SINGLETON);

            bind(org.openid4java.util.HttpFetcher.class)
                            .to(Openid4javaFetcher.class)
                            .in(Scopes.SINGLETON);
        }
    }

    @Provides
    @Singleton
    public URLFetchService provideUrlFetchService() {
        return URLFetchServiceFactory.getURLFetchService();
    }
}

