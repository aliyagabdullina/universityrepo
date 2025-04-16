<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="/styles/styleDataPages.css">
    </head>
    <body>
        <selection>
            <div class="container">
                <div class="menu">
                    <h1>Scheduling</h1>
                    <p class="university">UNIVERSITY</p>
                    <div class="menu-buttons-container">
                        <button class="menu-button icon" onclick="window.location.href='/account'">
                            <img src="/images/account.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Аккаунт</h4>
                        </button>
                        <button class="menu-button icon" onclick="window.location.href='/schedule'">
                            <img src="/images/scheduling.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Расписание</h4>
                        </button>
                        <button class="menu-button icon highlight" onclick="window.location.href='/data'">
                            <img src="/images/data.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Данные</h4>
                        </button>
                        <button class="menu-button icon" onclick="window.location.href='/timeslot'">
                            <img src="/images/time.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Сетка пар</h4>
                        </button>
                    </div>
                    <div class="menu-buttons-container-bottom">
                        <button class="menu-button icon" onclick="window.location.href='/settings'">
                            <img src="/images/setting.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Настройки</h4>
                        </button>
                    </div>
                </div>
                <div></div>
                <div class="body">
                    <div class="header-create icon-back">
                        <h2>Редактирование данных</h2>
                        <button class="button-none" type="button" onclick="window.history.back()">
                            <img src="/images/back.svg" alt="Аккаунт" width="25" height="25">
                        </button>
                    </div>
                    <div class="main">
                        <form action="/teacher/create" method="post">
                            <div class="input-block">
                                 <h5>Студенты</h5>
                                 <div class="select-block">
                                     <select name="studentId" multiple>
                                         <option value="" disabled selected>Выберите студентов</option>
                                         <#list students as student>
                                             <option value="${student.studentId}">${student.name}</option>
                                         </#list>
                                     </select>
                                 </div>
                             </div>
                            <div class="input-block">
                                 <h5>Программы</h5>
                                 <div class="select-block">
                                     <select name="programId" multiple>
                                         <option value="" disabled selected>Выберите программы</option>
                                         <#list programs as program>
                                             <option value="${program.programId}">${program.name}</option>
                                         </#list>
                                     </select>
                                 </div>
                             </div>
                            <div class="input-block">
                                 <h5>Дополнительные предметы</h5>
                                 <div class="select-block">
                                     <select name="courseId" multiple>
                                         <option value="" disabled selected>Выберите предметы</option>
                                         <#list courses as course>
                                             <option value="${course.courseId}">${course.name}</option>
                                         </#list>
                                     </select>
                                 </div>
                             </div>
                            <div class="input-block">
                                <h5>Кол-во часов</h5><input type="text" name="hours" placeholder="Введите кол-во часов"/>
                            </div>


                            <div class="footer">
                            <div class="footer-buttons-container">

                                <button class="footer-button" type="button" onclick="window.history.back()">
                                    <h4>Назад</h4>
                                </button>
                              <div class="footer-right">
                                    <button type="submit" class="footer-button save-button">
                                        <h4>Сохранить</h4>
                                    </button>
                              </div>
                            </div>
                        </div>
                        </form>
                    </div>
                </div>
            </div>
        </selection>
    </body>
</html>
