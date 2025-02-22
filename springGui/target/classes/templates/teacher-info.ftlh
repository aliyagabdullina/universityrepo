<!DOCTYPE html>
<html>
<head>
    <title>Teachers</title>
</head>
<body>
<h1>Teachers</h1><hr>
<h4>Подробная информация о учителе</h4>
<b>Имя учителя: </b>${teacher.name}<br>
<b>Кабинет учителя: </b>${teacher.place}<br>
<b>Количество уроков: </b>${teacher.numberOfLessons}<br>
<b>Временной слот: </b>${teacher.timeslot}<br>
<hr>
<form action="/teacher/delete/${teacher.teacherId}" method="post">
    <input type="submit" value="Удалить учителя"/>
</form>
</body>
</html>
