/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.account;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.GoodDataException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static com.gooddata.util.ResourceUtils.readStringFromResource;
import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class AccountServiceIT extends AbstractGoodDataIT {

    private static final String CREATE_ACCOUNT = "/account/create-account.json";
    private static final String ACCOUNT = "/account/account.json";
    private static final String ACCOUNT_UPDATE = "/account/update-account.json";
    private static final String ACCOUNT_ID = "ID";
    private static final String ACCOUNT_URI = Account.TEMPLATE.expand(ACCOUNT_ID).toString();
    private static final String CURRENT_ACCOUNT_URI = Account.TEMPLATE.expand(Account.CURRENT_ID).toString();
    private static final String LOGOUT_CURRENT = Account.LOGIN_TEMPLATE.expand(ACCOUNT_ID).toString();
    private static final String DOMAIN = "default";

    private static Account account;
    private static Account createAccount;

    @BeforeClass
    public void init() throws IOException {
        account = readObjectFromResource(ACCOUNT, Account.class);
        createAccount = readObjectFromResource(CREATE_ACCOUNT, Account.class);
    }

    @Test
    public void shouldCreateAccount() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(Account.ACCOUNTS_TEMPLATE.expand(DOMAIN).toString())
                .respond()
                .withBody("{\"uri\": \"" + ACCOUNT_URI + "\"}")
                .withStatus(201);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ACCOUNT_URI)
                .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(200);

        final Account created = gd.getAccountService().createAccount(createAccount, DOMAIN);

        assertThat(created, notNullValue());
        assertThat(created.getFirstName(), is("Blah"));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailToCreateAccount() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(Account.ACCOUNTS_TEMPLATE.expand(DOMAIN).toString())
                .respond()
                .withBody("")
                .withStatus(400);

        gd.getAccountService().createAccount(createAccount, DOMAIN);
    }

    @Test
    public void shouldRemoveAccount() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(ACCOUNT_URI)
                .respond()
                .withStatus(204);

        gd.getAccountService().removeAccount(account);
    }

    @Test(expectedExceptions = AccountNotFoundException.class)
    public void shouldFailToFindAccountForRemoval() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(ACCOUNT_URI)
                .respond()
                .withStatus(404);

        gd.getAccountService().removeAccount(account);
    }

    @Test
    public void shouldGetCurrentAccount() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(CURRENT_ACCOUNT_URI)
                .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(200);

        final Account current = gd.getAccountService().getCurrent();

        assertThat(current, notNullValue());
        assertThat(current.getFirstName(), is("Blah"));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailToGetCurrentAccount() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(CURRENT_ACCOUNT_URI)
                .respond()
                .withStatus(400);

        gd.getAccountService().getCurrent();
    }

    @Test(expectedExceptions = AccountNotFoundException.class)
    public void shouldFailToFindCurrentAccount() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(CURRENT_ACCOUNT_URI)
                .respond()
                .withStatus(404);

        gd.getAccountService().getCurrent();
    }

    @Test
    public void shouldLogout() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(CURRENT_ACCOUNT_URI)
                .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(200);
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(LOGOUT_CURRENT)
                .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(204);

        gd.getAccountService().logout();
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailToLogout() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(CURRENT_ACCOUNT_URI)
                .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(200);
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(LOGOUT_CURRENT)
                .respond()
                .withStatus(400);

        gd.getAccountService().logout();
    }

    @Test
    public void shouldGetAccount() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ACCOUNT_URI)
                .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(200);

        final Account created = gd.getAccountService().getAccountById(ACCOUNT_ID);

        assertThat(created, notNullValue());
        assertThat(created.getFirstName(), is("Blah"));
    }

    @Test(expectedExceptions = AccountNotFoundException.class)
    public void shouldFailToFindAccount() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ACCOUNT_URI)
                .respond()
                .withStatus(404);

        gd.getAccountService().getAccountById(ACCOUNT_ID);
    }

    @Test(expectedExceptions = AccountNotFoundException.class)
    public void shouldFailOnUpdateNonExistentAccount() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ACCOUNT_URI)
                .respond()
                .withStatus(404);

        gd.getAccountService().updateAccount(account);
    }

    @Test
    public void shouldUpdateAccount() throws Exception {
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(ACCOUNT_URI)
                .havingBody(jsonEquals(readStringFromResource(ACCOUNT_UPDATE)))
                .respond()
                .withStatus(200);

        final Account toBeUpdated = readObjectFromResource(ACCOUNT, Account.class);

        final String newFirstName = "newFirstName2";
        final String newEmail = "fake2@gooddata.com";
        final String newPass = "password2";
        final String newLastName = "Muhehe2";

        toBeUpdated.setFirstName(newFirstName);
        toBeUpdated.setEmail(newEmail);
        toBeUpdated.setPassword(newPass);
        toBeUpdated.setVerifyPassword(newPass);
        toBeUpdated.setLastName(newLastName);

        gd.getAccountService().updateAccount(toBeUpdated);
    }

}
