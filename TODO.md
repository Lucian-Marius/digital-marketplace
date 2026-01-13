# Chunked Upload Implementation Plan

## Backend Changes
- [ ] Add resumable.js dependency to pom.xml
- [ ] Create ChunkUploadController for handling chunk endpoints
- [ ] Modify StorageService to handle chunk assembly
- [ ] Add chunk metadata model/entity
- [ ] Update SellerController to support chunked uploads

## Frontend Changes
- [ ] Add resumable.js library to static/js
- [ ] Update upload.html template to use chunked upload
- [ ] Add progress bar and resume functionality
- [ ] Handle chunk size configuration

## Configuration
- [ ] Update application.properties for chunk settings
- [ ] Configure chunk size limits

## Testing
- [ ] Test chunked upload with large files
- [ ] Verify resume functionality
- [ ] Test error handling for failed chunks
