package com.spotisee.app.resources;

import com.spotisee.app.dao.UploadDao;
import com.spotisee.app.managers.UploadDataManager;
import com.spotisee.app.managers.UserValidationManager;
import com.spotisee.app.models.User;
import com.spotisee.app.models.dao.UploadInfo;
import com.spotisee.app.models.requests.UpdateUploadRequest;
import io.dropwizard.auth.Auth;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipInputStream;


@Path("api/upload-data")
@Produces(MediaType.APPLICATION_JSON)
public class UploadDataResource {

    private static final Logger log = LoggerFactory.getLogger(UploadDataResource.class);
    private final UploadDataManager uploadDataManager;
    private final UserValidationManager userValidationManager;


    public UploadDataResource(UploadDataManager uploadDataManager, UserValidationManager userValidationManager) {
        this.uploadDataManager = uploadDataManager;
        this.userValidationManager = userValidationManager;
    }

    @GET
    public Response listUploads(@Auth User user) {
        List<UploadInfo> uploadInfo = uploadDataManager.getUploads(user.getUserId());
        return Response.ok(uploadInfo).build();
    }

    @GET
    @Path("/{uploadId}")
    public Response getUpload(@Auth User user, @PathParam("uploadId") long uploadId) {
        userValidationManager.validateUserHasUpload(user, uploadId);
        Optional<UploadInfo> uploadInfo = uploadDataManager.getUpload(uploadId);
        if (uploadInfo.isEmpty()) {
            return Response.noContent().build();
        }
        return Response.ok(uploadInfo.get()).build();
    }

    @PUT
    @Path("/{uploadId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUploadName(@Auth User user,
                                     @PathParam("uploadId") long uploadId,
                                     @NotNull UpdateUploadRequest updateUploadRequest) {
        userValidationManager.validateUserHasUpload(user, uploadId);
        uploadDataManager.updateUpload(uploadId, updateUploadRequest.getUploadName());
        return Response.ok().build();
    }

    @DELETE
    @Path("/{uploadId}")
    public Response deleteUpload(@Auth User user, @PathParam("uploadId") long uploadId) {
        userValidationManager.validateUserHasUpload(user, uploadId);
        uploadDataManager.deleteUpload(uploadId);
        return Response.ok().build();
    }

    @POST
    @Path("/select/{uploadId}")
    public Response selectUpload(@Auth User user, @PathParam("uploadId") long uploadId) {
        userValidationManager.validateUserHasUpload(user, uploadId);
        uploadDataManager.setActiveUpload(user.getUserId(), uploadId);
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadZip(@Auth User user,
                              @FormDataParam("file") InputStream file
    ) {

        log.info("File Uploaded {}", file);
        long uploadId;
        try (ZipInputStream zipInputStream = new ZipInputStream(file)) {
            uploadId = uploadDataManager.storeZipFile(zipInputStream, user.getUserId());
        } catch (IOException e) {
            log.error("throwing {}", e.getMessage());
            return Response.status(400, String.format("Error loading jsons from zip file %s", e.getMessage())).build();
        }

        return Response.accepted(uploadId).build();
    }


}
