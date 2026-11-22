package com.pkg.tasktracker.controller;

import com.pkg.tasktracker.dto.AttachmentResponse;
import com.pkg.tasktracker.dto.DownloadableFile;
import com.pkg.tasktracker.service.AttachmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AttachmentControllerTest {
    private AttachmentService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(AttachmentService.class);
        mvc = MockMvcBuilders.standaloneSetup(new AttachmentController(service)).build();
    }

    @Test void uploadAttachment() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "notes.txt", "text/plain", "hello".getBytes());
        AttachmentResponse response = new AttachmentResponse(3L, 5L, "notes.txt", "text/plain", 5,
                1L, "Test User", Instant.now(), "/api/v1/attachments/3/download");
        when(service.upload(5L, file)).thenReturn(response);

        mvc.perform(multipart("/api/v1/tasks/5/attachments").file(file))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(3));
    }

    @Test void listAttachments() throws Exception {
        when(service.listForTask(5L)).thenReturn(List.of());
        mvc.perform(get("/api/v1/tasks/5/attachments")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test void downloadAttachment() throws Exception {
        DownloadableFile file = new DownloadableFile(new ByteArrayResource("hello".getBytes()), "notes.txt", "text/plain", 5);
        when(service.getForDownload(3L)).thenReturn(file);

        mvc.perform(get("/api/v1/attachments/3/download"))
                .andExpect(status().isOk()).andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("notes.txt")))
                .andExpect(content().string("hello"));
    }

    @Test void deleteAttachment() throws Exception {
        mvc.perform(delete("/api/v1/attachments/3")).andExpect(status().isNoContent());
        verify(service).delete(3L);
    }
}
