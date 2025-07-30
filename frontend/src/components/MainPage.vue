<template>
  <div class="container py-5">
    <h1 class="text-center mb-4 text-primary fw-bold">Document Search Assistant</h1>

    <section class="mb-5">
      <h2 class="h4 text-secondary mb-3 border-bottom pb-2">1. Upload PDF</h2>
      <input
        type="file"
        class="form-control"
        @change="handleFileUpload"
        accept=".pdf"
      />
      <button
        class="btn btn-primary mt-3"
        @click="uploadFile"
        :disabled="!selectedFile || uploading"
      >
        <span v-if="uploading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
        Upload
      </button>
      <p v-if="docId" class="mt-3 text-success">
        Document Uploaded. Doc ID: <code>{{ docId }}</code>
      </p>
    </section>

    <section v-if="docId" class="mb-5">
      <h2 class="h4 text-secondary mb-3 border-bottom pb-2">2. Ask Question</h2>
      <textarea
        v-model="query"
        class="form-control"
        rows="4"
        placeholder="Sorunuzu yazın..."
      ></textarea>
      <button
        class="btn btn-primary mt-3"
        @click="askQuestion"
        :disabled="!query || asking"
      >
        <span v-if="asking" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
        Send
      </button>
    </section>

    <section v-if="answer">
      <h2 class="h4 text-secondary mb-3 border-bottom pb-2">Answer:</h2>
      <div class="alert alert-light border">
        {{ answer }}
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const selectedFile = ref(null)
const docId = ref('')
const query = ref('')
const answer = ref('')

const uploading = ref(false)
const asking = ref(false)

function handleFileUpload(event) {
  selectedFile.value = event.target.files[0]
}

async function uploadFile() {
  if (!selectedFile.value) return

  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    const res = await axios.post('http://localhost:9090/document/extract', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    docId.value = res.data
    answer.value = ''
    query.value = ''
  } catch (error) {
    console.error(error)
    alert('Dosya yüklenirken hata oluştu.')
  } finally {
    uploading.value = false
  }
}

async function askQuestion() {
  if (!query.value || !docId.value) return

  asking.value = true
  try {
    const res = await axios.post('http://localhost:9090/document/findSimilar', {
      query: query.value,
      docId: docId.value,
    })
    answer.value = res.data
  } catch (error) {
    console.error(error)
    alert('Soru sorulurken hata oluştu.')
  } finally {
    asking.value = false
  }
}
</script>

<style>
body {
  background-color: #f8f9fa;
}
</style>
