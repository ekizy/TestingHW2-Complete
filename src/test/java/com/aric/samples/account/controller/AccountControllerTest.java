package com.aric.samples.account.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.aric.samples.account.model.Account;
import com.aric.samples.account.service.AccountService;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    public ObjectMapper mapper;
    public List<Account> mockAccounts;

    private static String ERROR_MESSAGE = "Required long parameter 'tckn' is not present";
    private static int ERROR_STATUS = 400;

    private static List<Account> emptyAccountList = new ArrayList<Account>();

    @MockBean
    private AccountService service;


    @Before
    public  void setUpFields(){
        mockAccounts = new ArrayList<Account>();
        Account account1 = new Account();
        account1.setId(1);
        account1.setBalance(120);
        account1.setOwnerFirstName("Yusuf");
        account1.setOwnerLastName("Ekiz");
        account1.setOwnerTckn(1);
        mockAccounts.add(account1);

        Account account2 = new Account();
        account2.setId(2);
        account2.setBalance(220);
        account2.setOwnerFirstName("Ceyda");
        account2.setOwnerLastName("Aladağ");
        account2.setOwnerTckn(1);
        mockAccounts.add(account2);


        Mockito.when(service.findPersonsByTckn(1)).thenReturn(mockAccounts);
        Mockito.when(service.findPersonsByTckn(2)).thenReturn(emptyAccountList);
        Mockito.when(service.deposit(new Long(1),100.0)).thenReturn(mockAccounts.get(0));

        Mockito.when(service.deposit(new Long(2),100.0)).thenReturn(null);

        Mockito.when(service.eft(new Long(1),new Long(2),50.0)).thenReturn(mockAccounts.get(1));
        Mockito.when(service.eft(new Long(1),new Long(3),50.0)).thenReturn(null);

        mapper = new ObjectMapper();
    }

    @Test
    public void testAccountSuccess() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/query?tckn=1");

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        String jsonStringOfAccountList = mapper.writeValueAsString(mockAccounts);

        assertEquals(responseBody,jsonStringOfAccountList);

    }

    @Test
    public void testAccountFail() throws Exception {
        // No tckn input for query route

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/query");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(response.getStatus(),ERROR_STATUS);
        assertEquals(response.getErrorMessage(),ERROR_MESSAGE);
    }

    @Test
    public void testAccountNoResult() throws Exception {
        // No tckn input for query route

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/query?tckn=2");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        String jsonStringOfEmptyList = mapper.writeValueAsString(emptyAccountList);

        assertEquals(responseBody,jsonStringOfEmptyList);
    }


    @Test
    public void testDepositSuccess() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/deposit?id=1&amount=100.0");

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        String jsonStringAccount1 = mapper.writeValueAsString(mockAccounts.get(0));

        assertEquals(responseBody,jsonStringAccount1);

    }

    @Test
    public void testDepositNoResult() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/deposit?id=2&amount=100.0");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertNull(response.getContentType());

    }

    @Test
    public void testDepositBadRequest() throws Exception {

        //No inputs are given, it will give bad request error.
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/deposit");

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    public void testWithdrawSuccess() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eft?senderId=1&receiverId=2&amount=50.0");

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        String jsonStringAccount2 = mapper.writeValueAsString(mockAccounts.get(1));

        assertEquals(responseBody,jsonStringAccount2);
    }

    @Test
    public void testWithdrawNoResult() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/deposit?id=2&amount=100.0");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertNull(response.getContentType());

    }

    @Test
    public void testWithdrawBadRequest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/deposit");

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();

    }


}
