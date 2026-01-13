/**
 * Chunked File Upload Library
 * Handles large file uploads by splitting them into chunks
 */
class ChunkedUploader {
    constructor(options = {}) {
        this.chunkSize = options.chunkSize || 1024 * 1024; // 1MB default
        this.maxRetries = options.maxRetries || 3;
        this.uploadUrl = options.uploadUrl || '/api/upload';
        this.onProgress = options.onProgress || (() => {});
        this.onComplete = options.onComplete || (() => {});
        this.onError = options.onError || (() => {});
    }

    /**
     * Upload a file using chunked upload
     */
    async uploadFile(file, folder = 'products') {
        try {
            // Initialize upload
            const uploadId = await this.initializeUpload(file, folder);

            // Calculate chunks
            const totalChunks = Math.ceil(file.size / this.chunkSize);

            // Upload chunks
            for (let chunkNumber = 0; chunkNumber < totalChunks; chunkNumber++) {
                const start = chunkNumber * this.chunkSize;
                const end = Math.min(start + this.chunkSize, file.size);
                const chunk = file.slice(start, end);

                await this.uploadChunk(uploadId, chunk, chunkNumber, totalChunks, file, folder);

                // Update progress
                const progress = Math.round(((chunkNumber + 1) / totalChunks) * 100);
                this.onProgress(progress, chunkNumber + 1, totalChunks);
            }

            // Complete upload
            const result = await this.completeUpload(uploadId, folder);
            this.onComplete(result);

            return result;

        } catch (error) {
            this.onError(error);
            throw error;
        }
    }

    /**
     * Initialize the upload
     */
    async initializeUpload(file, folder) {
        const response = await fetch(`${this.uploadUrl}/init`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                fileName: file.name,
                fileSize: file.size,
                contentType: file.type || 'application/octet-stream',
                totalChunks: Math.ceil(file.size / this.chunkSize)
            })
        });

        if (!response.ok) {
            throw new Error(`Failed to initialize upload: ${response.statusText}`);
        }

        const data = await response.json();
        return data.uploadId;
    }

    /**
     * Upload a single chunk
     */
    async uploadChunk(uploadId, chunk, chunkNumber, totalChunks, file, folder) {
        const formData = new FormData();
        formData.append('uploadId', uploadId);
        formData.append('chunkNumber', chunkNumber);
        formData.append('totalChunks', totalChunks);
        formData.append('fileName', file.name);
        formData.append('contentType', file.type || 'application/octet-stream');
        formData.append('totalFileSize', file.size);
        formData.append('chunk', chunk);

        let retries = 0;
        while (retries < this.maxRetries) {
            try {
                const response = await fetch(`${this.uploadUrl}/chunk`, {
                    method: 'POST',
                    body: formData
                });

                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }

                const data = await response.json();

                if (data.status === 'chunk_exists') {
                    // Chunk already uploaded, skip
                    return;
                }

                if (data.status === 'chunk_uploaded' || data.status === 'complete') {
                    return data;
                }

                throw new Error(`Unexpected response: ${data.status}`);

            } catch (error) {
                retries++;
                if (retries >= this.maxRetries) {
                    throw new Error(`Failed to upload chunk ${chunkNumber} after ${retries} retries: ${error.message}`);
                }

                // Wait before retry
                await new Promise(resolve => setTimeout(resolve, 1000 * retries));
            }
        }
    }

    /**
     * Complete the upload
     */
    async completeUpload(uploadId, folder) {
        const response = await fetch(`${this.uploadUrl}/complete`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                uploadId: uploadId,
                folder: folder
            })
        });

        if (!response.ok) {
            throw new Error(`Failed to complete upload: ${response.statusText}`);
        }

        const data = await response.json();

        if (data.status !== 'success') {
            throw new Error(`Upload completion failed: ${data.message || 'Unknown error'}`);
        }

        return data;
    }

    /**
     * Get upload progress
     */
    async getProgress(uploadId, totalChunks) {
        const response = await fetch(`${this.uploadUrl}/progress/${uploadId}?totalChunks=${totalChunks}`);

        if (!response.ok) {
            throw new Error(`Failed to get progress: ${response.statusText}`);
        }

        const data = await response.json();
        return data;
    }

    /**
     * Cancel upload
     */
    async cancelUpload(uploadId) {
        const response = await fetch(`${this.uploadUrl}/cancel/${uploadId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`Failed to cancel upload: ${response.statusText}`);
        }

        const data = await response.json();
        return data;
    }
}

// Export for use in other scripts
window.ChunkedUploader = ChunkedUploader;
