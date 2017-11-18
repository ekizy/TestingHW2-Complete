package com.aric.samples.account.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    public List<Account> initialAccounts;

    private static String ERROR_MESSAGE = "Required long parameter 'tckn' is not present";
    private static int ERROR_STATUS = 400;

    private static List<Account> emptyAccountList = new ArrayList<Account>();

    @MockBean
    private AccountService service;


    @Before
    public  void setUpFields(){
        initialAccounts = new ArrayList<Account>(); //Create inital account list with size 2
        Account account1 = new Account();
        account1.setId(1);
        account1.setBalance(120);
        account1.setOwnerFirstName("Yusuf");
        account1.setOwnerLastName("Ekiz");
        account1.setOwnerTckn(1);
        initialAccounts.add(account1);

        Account account2 = new Account();
        account2.setId(2);
        account2.setBalance(220);
        account2.setOwnerFirstName("Ceyda");
        account2.setOwnerLastName("Aladağ");
        account2.setOwnerTckn(1);
        initialAccounts.add(account2);


        Mockito.when(service.findPersonsByTckn(1)).thenReturn(initialAccounts); // Mock web service calls in order to test controller
        Mockito.when(service.findPersonsByTckn(2)).thenReturn(emptyAccountList); //It should return empty list because no accounts with tckn = 2
        Mockito.when(service.deposit(new Long(1),100.0)).thenReturn(initialAccounts.get(0)); // mock deposit call
        Mockito.when(service.deposit(new Long(2),100.0)).thenReturn(null);

        Mockito.when(service.eft(new Long(1),new Long(2),50.0)).thenReturn(initialAccounts.get(1)); // mock eft call
        Mockito.when(service.eft(new Long(1),new Long(3),50.0)).thenReturn(null);

        mapper = new ObjectMapper();
    }

    @Test
    public void testAccountSuccess() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/query?tckn=1"); //Controller request

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn(); //Expect no error for this route

        String responseBody = result.getResponse().getContentAsString(); //Response body

        String jsonStringOfAccountList = mapper.writeValueAsString(initialAccounts); //expected body

        assertEquals(responseBody,jsonStringOfAccountList);

    }

    @Test
    public void testAccountFail() throws Exception {
        // No tckn input for query route

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/query"); //Controller request

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(response.getStatus(),ERROR_STATUS); // For this route, we expect an error with code 400
        assertEquals(response.getErrorMessage(),ERROR_MESSAGE); //Also response will hava an error message
    }

    @Test
    public void testAccountNoResult() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/query?tckn=2"); // It should return empty list

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String responseBody = result.getResponse().getContentAsString(); // body content as JSON string

        String jsonStringOfEmptyList = mapper.writeValueAsString(emptyAccountList); // empty list as JSON strimg

        assertEquals(responseBody,jsonStringOfEmptyList);
    }


    @Test
    public void testDepositSuccess() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/deposit?id=1&amount=100.0");

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn(); // expect request is successfull

        String responseBody = result.getResponse().getContentAsString();

        String jsonStringAccount1 = mapper.writeValueAsString(initialAccounts.get(0));

        assertEquals(responseBody,jsonStringAccount1); // compare response body and expected body

    }

    @Test
    public void testDepositNoResult() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/deposit?id=2&amount=100.0");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn(); // In this call we expect null from controller.

        MockHttpServletResponse response = result.getResponse();

        assertNull(response.getContentType()); // check the response content type null or not

    }

    @Test
    public void testDepositBadRequest() throws Exception {

        //No inputs are given for deposit so it will give bad request error.
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/deposit");

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn(); //check it is bad request or not

    }

    @Test
    public void testWithdrawSuccess() throws Exception {

        //successfull eft action.
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eft?senderId=1&receiverId=2&amount=50.0");

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn(); //expect request is successful

        String responseBody = result.getResponse().getContentAsString();

        String jsonStringAccount2 = mapper.writeValueAsString(initialAccounts.get(1));

        assertEquals(responseBody,jsonStringAccount2); //compare actual body and expected body content
    }

    @Test
    public void testWithdrawNoResult() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/deposit?id=2&amount=100.0");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertNull(response.getContentType()); //expect body content to be null

    }

    @Test
    public void testWithdrawBadRequest() throws Exception {

        //No inputs are given for eft, so it will give bad request error
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/deposit");

        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();

    }


}
