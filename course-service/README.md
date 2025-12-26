# Course Service Domain Overview

Kurs servisi uchun asosiy modellarning o‘zaro bog‘lanishi va ma’lumot oqimlari.

## Asosiy entitilar
- **Category**: kurslarni mavzu bo‘yicha guruhlaydi. Maydonlar: `id`, `name`, `description`, `createdAt`.
- **Course**: asosiy kurs. Maydonlar: `id`, `category_id`, `title`, `slug`, `summary`, `level`, `published`, `publishedAt`, `createdAt`, `updatedAt`.
- **CourseDetail**: batafsil tavsif. Maydonlar: `id`, `course_id`, `description`, `objectives`, `prerequisites`, `updatedAt`.
- **CourseInstructor**: kursga biriktirilgan o‘qituvchi(lar). Maydonlar: `id`, `course_id`, `teacher_id`, `role`, `assignedAt`.
- **CoursePublish**: nashr holati tarixi. Maydonlar: `id`, `course_id`, `status`, `note`, `changedBy`, `changedAt`.
- **CourseTag**: kurs teglar. Maydonlar: `id`, `course_id`, `tag`, `createdAt` (unikal course+tag).
- **CourseModule**: kurs bo‘limlari. Maydonlar: `id`, `course_id`, `title`, `orderIndex`, `createdAt`.
- **Lesson**: modul ichidagi dars. Maydonlar: `id`, `module_id`, `title`, `summary`, `orderIndex`, `durationMinutes`, `createdAt`.
- **LessonContent**: darsning asosiy kontenti. Maydonlar: `id`, `lesson_id`, `body`, `videoUrl`, `slideUrl`, `updatedAt`.
- **LessonResource**: darsga qo‘shimcha material. Maydonlar: `id`, `lesson_id`, `title`, `url`, `type`, `addedAt`.
- **CourseEnrollment**: o‘quvchining kursga yozilishi. Maydonlar: `id`, `course_id`, `student_id`, `enrolledAt`, `completedAt`, `active`.
- **Progress entitilari**:
  - **LessonProgress**: `id`, `lesson_id`, `student_id`, `status`, `completedAt`, `progressPercent`.
  - **ModuleProgress**: `id`, `module_id`, `student_id`, `status`, `completedAt`, `progressPercent`.
  - **CourseProgress**: `id`, `course_id`, `student_id`, `status`, `completedAt`, `progressPercent`.
- **Assessment stack**:
  - **Assessment**: `id`, `course_id`, `type`, `title`, `instructions`, `weightPercent`, `createdAt`.
  - **AssessmentRule**: `id`, `assessment_id`, `description`.
  - **AssessmentCriteria**: `id`, `assessment_id`, `label`, `maxScore`, `weightPercent`.
  - **AssessmentScore**: `id`, `assessment_id`, `student_id`, `score`, `gradedAt`.
  - **AssessmentResult**: `id`, `assessment_id`, `student_id`, `totalScore`, `passed`, `calculatedAt`.

## Bog‘lanishlar (high-level)
- Category 1—N Course
- Course 1—1 CourseDetail
- Course 1—N CourseInstructor (teacher_id)
- Course 1—N CoursePublish (tarix)
- Course 1—N CourseTag
- Course 1—N CourseModule
- CourseModule 1—N Lesson
- Lesson 1—1 LessonContent
- Lesson 1—N LessonResource
- Course 1—N CourseEnrollment (student_id)
- Lesson/Module/Course — N LessonProgress/ModuleProgress/CourseProgress (student_id)
- Course 1—N Assessment
- Assessment 1—N AssessmentRule, AssessmentCriteria, AssessmentScore, AssessmentResult

## Teacher identifikatsiyasi
- JWT `JwtUser` ichidan `userId` olinadi.
- Yangi kurs yaratishda bodyga teacherId bermasdan, service token egasini `CourseInstructor.teacherId` (va kerak bo‘lsa Course.primaryTeacherId) sifatida yozadi.
- Teacher o‘z kurslarini olish: `CourseInstructorRepository.findByTeacherId(userId)` yoki `CourseRepository`da primaryTeacherId bo‘yicha filtr.

## Nashr jarayoni
- CoursePublish yozuvlari status tarixini saqlaydi (DRAFT/REVIEW/PUBLISHED/UNPUBLISHED).
- Course.publish flag + publishedAt kursning joriy holatini bildiradi; tarix CoursePublish orqali yuritiladi.

## Identifikatorlar va vaqtlar
- Barcha PK/FK lar UUID.
- Vaqt maydonlari `LocalDateTime` bilan saqlanadi.

## Misol: "Backend Foundations" kursi
- **Course**: `title=Backend Foundations`, `slug=backend-foundations`, `summary=Java va SQL asoslarini o‘rgatadigan kirish kursi`, `level=BEGINNER`, `published=true`, `publishedAt=2025-01-10T09:00:00`.
- **CourseDetail**: `description=HTTP, REST, ORM va ma’lumotlar bazasi konseptlari`, `objectives=REST API qurish, JPA bilan CRUD, tranzaksiyalar`, `prerequisites=Java sintaksisi, basic OOP`.
- **CourseInstructor**: `teacher_id=<JWT dan olingan o‘qituvchi UUID>`, `role=Lead Instructor`.
- **CourseTag**: `["java", "spring", "sql"]`.
- **CourseModule**:  
  - `orderIndex=1`, `title=Java Asoslari`  
  - `orderIndex=2`, `title=REST va Spring`  
  - `orderIndex=3`, `title=Ma’lumotlar bazasi va JPA`
- **Lesson** (modul 2):  
  - `orderIndex=1`, `title=REST prinsiplari`, `durationMinutes=30`  
  - `orderIndex=2`, `title=Spring Boot starter`, `durationMinutes=40`
- **LessonContent**: dars `REST prinsiplari` uchun `body` (matn), `videoUrl` (vidеo havola), `slideUrl` (ixtiyoriy).
- **LessonResource**: dars `Spring Boot starter` uchun `title=Demo repo`, `url=https://github.com/...`, `type=link`.
- **CoursePublish**: tarix: `DRAFT -> REVIEW -> PUBLISHED`, `changedBy=<user UUID>`, `changedAt=<vaqt>`.
- **CourseEnrollment**: `student_id=<o‘quvchi UUID>`, `enrolledAt=<vaqt>`, `active=true`.
- **Progress**:  
  - LessonProgress: `status=COMPLETED`, `progressPercent=100`, `completedAt=<vaqt>`  
  - CourseProgress: `status=IN_PROGRESS`, `progressPercent=45`
- **Assessment** (ixtiyoriy): `type=QUIZ`, `title=REST fundamentals quiz`, `weightPercent=15`.

## Tezkor namunaviy ma'lumotlar (soddalashtirilgan JSON)
Har bir jadval uchun namunaviy recordlar (maydonlar entitidagi kolonlarga mos):
- **Category** — `{ id: 1, name: "Backend Development", slug: "backend-development", orderNumber: 1 }`
- **Course** — `{ id: 101, categoryId: 1, title: "Java Spring Boot from Zero to Pro", slug: "java-spring-boot", summary: "Spring Boot 3 bilan real loyihalar", level: "BEGINNER", published: true, publishedAt: "2025-01-10T09:00:00" }`
- **CourseDetail** — `{ id: 201, courseId: 101, description: "Spring Boot 3 bilan real loyihalar", objectives: "REST API, JPA CRUD, tranzaksiyalar", prerequisites: "Java Core" }`
- **CourseInstructor** — `{ id: 301, courseId: 101, teacherId: 10, role: "MAIN_TEACHER", assignedAt: "2025-01-05T10:00:00" }`
- **CoursePublish** — `{ id: 401, courseId: 101, status: "PUBLISHED", note: "QC approved", changedBy: 10, changedAt: "2025-01-10T09:00:00" }`
- **CourseTag** — `{ id: 501, courseId: 101, tag: "spring-boot", createdAt: "2025-01-05T11:00:00" }`
- **CourseModule** — `{ id: 1001, courseId: 101, title: "Spring Boot Basics", orderIndex: 1, createdAt: "2025-01-06T09:00:00" }`
- **Lesson** — `{ id: 5001, moduleId: 1001, title: "Spring Boot nima?", summary: "Framework asoslari", orderIndex: 1, durationMinutes: 30, createdAt: "2025-01-06T10:00:00" }`
- **LessonContent** — `{ id: 5101, lessonId: 5001, body: "REST va Spring Boot asoslari...", videoUrl: "https://cdn.codebyz.online/video1.mp4", slideUrl: "https://cdn.codebyz.online/intro.pdf", updatedAt: "2025-01-06T12:00:00" }`
- **LessonResource** — `{ id: 5201, lessonId: 5001, title: "Demo repo", url: "https://github.com/org/demo", type: "link", addedAt: "2025-01-06T12:30:00" }`
- **CourseEnrollment** — `{ id: 6001, courseId: 101, studentId: 2001, enrolledAt: "2025-02-01T08:00:00", completedAt: null, active: true }`
- **LessonProgress** — `{ id: 6101, lessonId: 5001, studentId: 2001, status: "COMPLETED", completedAt: "2025-02-02T09:00:00", progressPercent: 100 }`
- **ModuleProgress** — `{ id: 6201, moduleId: 1001, studentId: 2001, status: "IN_PROGRESS", completedAt: null, progressPercent: 60 }`
- **CourseProgress** — `{ id: 6301, courseId: 101, studentId: 2001, status: "IN_PROGRESS", completedAt: null, progressPercent: 35 }`
- **Assessment** — `{ id: 3001, courseId: 101, type: "QUIZ", title: "REST fundamentals quiz", instructions: "10 ta savol, 1 urinish", weightPercent: 15, createdAt: "2025-02-03T10:00:00" }`
- **AssessmentRule** — `{ id: 3002, assessmentId: 3001, description: "Passing score 70%, 3 attempts" }`
- **AssessmentCriteria** — `{ id: 3003, assessmentId: 3001, label: "To‘g‘ri javoblar foizi", maxScore: 100, weightPercent: 100 }`
- **AssessmentScore** — `{ id: 3004, assessmentId: 3001, studentId: 2001, score: 82, gradedAt: "2025-02-05T12:00:00" }`
- **AssessmentResult** — `{ id: 3005, assessmentId: 3001, studentId: 2001, totalScore: 82, passed: true, calculatedAt: "2025-02-05T12:05:00" }`
- **Quiz** — `{ id: 4001, lessonId: 5001, totalQuestions: 10 }`
- **QuizQuestion** — `{ id: 4101, quizId: 4001, question: "Spring Boot nimaga xizmat qiladi?" }`
- **QuizOption** — `{ id: 4201, questionId: 4101, text: "Microservice yaratish uchun", correct: true }`
- **QuizSubmission** — `{ id: 4301, quizId: 4001, studentId: 2001, submittedAt: "2025-02-05T11:50:00" }`
- **QuizAnswer** — `{ id: 4401, questionId: 4101, studentId: 2001, selectedOptionId: 4201 }`
- **QuizResult** — `{ id: 4501, quizId: 4001, studentId: 2001, score: 80 }`
- **Assignment** — `{ id: 6002, lessonId: 5002, title: "REST API yozish" }`
- **AssignmentCriteria** — `{ id: 6003, assignmentId: 6002, criteria: "CRUD to‘liq ishlashi" }`
- **AssignmentSubmission** — `{ id: 6004, assignmentId: 6002, studentId: 2001, githubUrl: "https://github.com/user/project" }`
- **AssignmentEvaluation** — `{ id: 6005, assignmentId: 6002, studentId: 2001, comment: "Yaxshi bajarilgan" }`
- **AssignmentScore** — `{ id: 6006, assignmentId: 6002, studentId: 2001, score: 90 }`
- **GradingScale** — `{ id: 7001, name: "100 ballik tizim" }`
- **Grade** — `{ id: 7002, minScore: 86, maxScore: 100, grade: "A" }`
- **PassingRule** — `{ id: 8001, courseId: 101, minProgressPercent: 80 }`
- **Certificate** — `{ id: 9001, studentId: 2001, courseId: 101, issuedAt: "2025-03-01T10:00:00" }`
- **CertificateTemplate** — `{ id: 9002, name: "Default Certificate", backgroundUrl: "https://cdn.codebyz.online/cert.png" }`
- **CompletionRequirement** — `{ id: 9003, courseId: 101, requiredProgress: 100, requiredAssessmentsPassed: true }`
- **CourseReview** — `{ id: 9501, courseId: 101, studentId: 2001, comment: "Juda foydali kurs" }`
- **CourseRating** — `{ id: 9502, courseId: 101, studentId: 2001, rating: 5 }`
