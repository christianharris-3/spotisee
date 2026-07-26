package com.spotisee.app.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotisee.app.dao.UploadDao;
import com.spotisee.app.managers.UploadDataManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Path("api/upload-data")
@Produces(MediaType.APPLICATION_JSON)
public class UploadDataResource {

    private static final Logger log = LoggerFactory.getLogger(UploadDataResource.class);
    private final UploadDataManager uploadDataManager;

    public UploadDataResource(UploadDao uploadDao) {
        this.uploadDataManager = new UploadDataManager(uploadDao);
    }

    @GET
    public Response listUploads() {
        return Response.accepted().build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadZip(
            @FormDataParam("file") InputStream file
    ) {
        log.info("File Uploaded {}", file);
        try (ZipInputStream zipInputStream = new ZipInputStream(file)) {
            uploadDataManager.storeZipFile(zipInputStream);
        } catch (IOException e) {
            return Response.status(400, "Error loading jsons from zip file").build();
        }

        return Response.accepted().build();
    }


}
