/*
 * Copyright 2016 the original author or authors.
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
package SpringUserService;

import SpringUserService.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {

    private String testContent = "{\"firstName\": \"Ivan\", \"lastName\":\"Petrov\"," +
                    "\"birthDate\": \"25.05.1994\", \"email\":\"ivanp@email.com\"," +
                    "\"password\": \"Qwerty123\"}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void deleteAllBeforeTests() throws Exception {
        userRepository.deleteAll();
    }

    @Test
    public void shouldReturnRepositoryIndex() throws Exception {

        mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
                jsonPath("$._links.users").exists());
    }

    @Test
    public void shouldCreateEntity() throws Exception {

        mockMvc.perform(post("/users").content(testContent)).andExpect(
                status().isCreated()).andExpect(
                header().string("Location", containsString("users/")));
    }

    @Test
    public void shouldRetrieveEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/users").content(testContent)).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
                jsonPath("$.firstName").value("Ivan")).andExpect(
                jsonPath("$.lastName").value("Petrov")).andExpect(
                jsonPath("$.birthDate").value("1994-05-24T20:00:00.000+0000")).andExpect(
                jsonPath("$.email").value("ivanp@email.com"));
    }

    @Test
    public void shouldQueryEntity() throws Exception {

        mockMvc.perform(post("/users").content(testContent)).andExpect(
                status().isCreated());

        mockMvc.perform(
                get("/users/search/findByEmail?email={email}", "ivanp@email.com")).andExpect(
                status().isOk()).andExpect(
                jsonPath("$._embedded.users[0].lastName").value(
                        "Petrov"));
    }

    @Test
    public void shouldUpdateEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/users").content(testContent)).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(put(location).content(
                "{\"firstName\": \"Vasiliy\", \"lastName\":\"Goncharov\"," +
                        "\"birthDate\": \"14.04.1976\", \"email\":\"goncharov_v@email.com\"," +
                        "\"password\": \"Qwerty123321\"}")).andExpect(
                status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
                jsonPath("$.firstName").value("Vasiliy")).andExpect(
                jsonPath("$.lastName").value("Goncharov")).andExpect(
                jsonPath("$.birthDate").value("1976-04-13T21:00:00.000+0000")).andExpect(
                jsonPath("$.email").value("goncharov_v@email.com"));
    }

    @Test
    public void shouldPartiallyUpdateEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/users").content(testContent)).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(
                patch(location).content("{\"lastName\": \"Ivanov\"}")).andExpect(
                status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
                jsonPath("$.firstName").value("Ivan")).andExpect(
                jsonPath("$.lastName").value("Ivanov")).andExpect(
                jsonPath("$.birthDate").value("1994-05-24T20:00:00.000+0000")).andExpect(
                jsonPath("$.email").value("ivanp@email.com"));
    }

    @Test
    public void shouldDeleteEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/users").content(testContent)).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(delete(location)).andExpect(status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isNotFound());
    }
}