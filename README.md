
# Document Search Assistant

Document Search Assistant is an AI-powered application that allows users to upload PDF documents and ask natural language questions about their content. It helps quickly and accurately find information within large and complex documents by automatically identifying relevant parts and providing clear answers.

---

## About the Project

Users upload PDF documents to the system. The system logically segments and analyzes the document. Users then ask questions in natural language. The system finds the most relevant parts of the document and the AI model generates the clearest and most direct answers. This makes searching for information in large documents fast and easy.

---

## How It Works

1. The user uploads a PDF file to the system.  
2. The system splits the PDF into logical chunks and converts each chunk into numerical vectors.  
3. The user sends a question in natural language to the system.  
4. The system also converts the question into a numerical vector.  
5. The question vector is compared with document chunk vectors to find the most relevant parts.  
6. The selected document chunks and the question are sent to the AI model.  
7. The AI model generates the clearest and most direct answer based on the information from the document.  
8. This answer is returned to the user.

---

## API Endpoints

### 1. Document Upload

```
POST http://localhost:9090/document/extract
```

**Parameters:**

- `file` : PDF file to upload (form-data)

**Response:**

- Returns a unique UUID string for the uploaded document if successful.

**Example:**

```json
"ed3c0659-6ee3-4eb2-819d-f57595293d20"
```

---

### 2. Document Query

```
POST http://localhost:9090/document/findSimilar
```

**Request Body (JSON):**

```json
{
  "query": "What actions can the user perform and what are the endpoints for these actions?",
  "docId": "ed3c0659-6ee3-4eb2-819d-f57595293d20"
}
```

**Description:**

You send a natural language question and the document’s UUID. The system finds the most relevant part of the document and generates a clear answer using the AI model.

**Response:**

Returns a string containing a clear and direct answer based on the document content.

**Example Response:**

```
The user can perform the following actions: upload files, make queries, and view results. Endpoints are /document/extract and /document/findSimilar.
```

---

## Configuration
Before running the backend of the project, you need to manually add your API keys to the src/main/resources/application.properties file as shown below:

**application.properties:**
```properties
jina.api.key=Bearer YOUR_JINA_API_KEY
gemini.api.key=YOUR_GEMINI_API_KEY
```
⚠️ Warning: Never commit the application.properties file to GitHub! This file is included in .gitignore and must remain private to protect your API keys.



## Setup and Run

### Backend (Spring Boot)

```bash
cd backend
./gradlew bootRun
```

### Frontend (Vue 3)

```bash
cd frontend
npm install
npm run dev
```

---

## License

This project is licensed under the MIT License.

---

Feel free to reach out if you have any questions or suggestions.
