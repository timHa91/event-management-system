package de.tim.evenmanagmentsystem.security.integration;

import de.tim.evenmanagmentsystem.security.repository.TokenRepository;
import de.tim.evenmanagmentsystem.security.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProtectedEndpointsIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private JwtService jwtService;

        @MockBean
        private TokenRepository tokenRepository;

        @Test
        public void shouldAllowAccessToPublicEndpoints() throws Exception {
                mockMvc.perform(get("/api/events"))
                                .andExpect(status().isOk());
        }

        @Test
        public void shouldDenyAccessToProtectedEndpointsForUnauthenticatedUsers() throws Exception {
                mockMvc.perform(get("/api/attendees/profile"))
                                .andExpect(status().isUnauthorized());

                mockMvc.perform(get("/api/organizers/events"))
                                .andExpect(status().isUnauthorized());

                mockMvc.perform(get("/api/admin/users"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "attendee@example.com", roles = "ATTENDEE")
        public void shouldAllowAccessToAttendeeEndpointsForAttendees() throws Exception {
                mockMvc.perform(get("/api/attendees/me"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "attendee@example.com", roles = "ATTENDEE")
        public void shouldDenyAccessToOrganizerEndpointsForAttendees() throws Exception {
                mockMvc.perform(get("/api/organizers/events"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "organizer@example.com", roles = "ORGANIZER")
        public void shouldAllowAccessToOrganizerEndpointsForOrganizers() throws Exception {
                mockMvc.perform(get("/api/organizers/me/events"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "organizer@example.com", roles = "ORGANIZER")
        public void shouldDenyAccessToAdminEndpointsForOrganizers() throws Exception {
                mockMvc.perform(get("/api/admin/users"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "admin@example.com", roles = "ADMIN")
        public void shouldAllowAccessToAdminEndpointsForAdmins() throws Exception {
                mockMvc.perform(get("/api/admin/users"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "admin@example.com", roles = { "ADMIN", "ORGANIZER" })
        public void shouldAllowAccessToMultipleRoleEndpointsForUsersWithMultipleRoles() throws Exception {
                mockMvc.perform(get("/api/admin/users"))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/api/organizers/me/events"))
                                .andExpect(status().isOk());
        }
}