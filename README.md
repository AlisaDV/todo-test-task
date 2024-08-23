Тестовое задание

Если дамп бд повредится или не загрузится, то в первую очередь (после запуска проекта) нужно добавить роль в таблицу roles:
INSERT INTO public.roles
(name)
VALUES('ROLE_USER');

Если дамп рабочий, то вот данные для авторизации двух пользователей:
test@test.com - email
useruser - password

dv@test.com - email
123 - password


-----------------------------------
/registration - регистрация 
{
  "username": "Alice",
  "password": "123",
  "email": "dv@test.com"
}
-----------------------------------
/auth - аутентификация
{
  "email": "dv@test.com"
  "password": "123",
{
-----------------------------------
/users - GET request, список пользователей
-----------------------------------
/unsecure, /info - GET request, проверка аутентификации (защищённые и незащищённые данные)
-----------------------------------
CRUD todo
/todos - POST request, создание
/todos/{id} - GET request, конкретная задача
/todos - GET request, список задач пользователя
/todos/{id} - PUT request, обновить задачу
/todos/{id} - DELETE request, удалить задачу

{
  "title": "title"
  "description": "description",
  "deadline": "2024:08:23"
{
-----------------------------------  
/transit - передача задачи другому пользователю
{
  "user_id": "1"
  "todo_id": "5",
{


-----------------------------------
Фатеев Илья
