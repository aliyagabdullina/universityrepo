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
                    <div class="menu-buttons-container">
                        <button class="menu-button icon" onclick="window.location.href='/account'">
                            <img src="/images/account.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Аккаунт</h4>
                        </button>
                        <button class="menu-button icon" onclick="window.location.href='/timeslot'">
                            <img src="/images/time.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Тайм слоты</h4>
                        </button>
                        <button class="menu-button icon highlight" onclick="window.location.href='/places'">
                            <img src="/images/data.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Редактирование данных</h4>
                        </button>
                        <button class="menu-button icon" onclick="window.location.href='/schedule'">
                            <img src="/images/scheduling.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Расписание</h4>
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
                                <h5>Имя учителя</h5> <input type="text" name="name"" placeholder="Введите ФИО"/><br>
                            </div>
                            <div class="input-block">
                                 <h5>Кабинет учителя</h5>
                                 <div class="select-block">
                                     <select name="placeId">
                                         <option value="" disabled selected>Выберите кабинет</option>
                                         <#list places as place>
                                             <option value="${place.placeId}">${place.name}</option>
                                         </#list>
                                     </select><br>
                                 </div>
                             </div>
                             <div class="input-block">
                                  <h5>Предметы</h5>
                                  <div class="select-block">
                                      <select name="courseId" multiple>
                                          <option value="" disabled selected>Выберите предметт</option>
                                          <#list courses as course>
                                              <option value="${course.courseId}">${course.name}</option>
                                          </#list>
                                      </select><br>
                                  </div>
                              </div>
                            <div class="input-block">
                                <h5>Количество уроков</h5><input type="text" name="maxNumberOfLessons" placeholder="Введите кол-во уроков"/><br>
                            </div>
                            <div class="input-block">
                                <h5>Временной слот</h5><input type="text" name="timeslotId" placeholder="Тут будет временной слот"/><br>
                            </div>
                            <div class="footer">
                                <div class="footer-buttons-container">
                                    <button type="submit" class="footer-button" onclick="window.location.href='/teachers'">
                                        <h4>Сохранить</h4>
                                    </button>
                                    <button class="footer-button" type="button" onclick="window.history.back()"><h4>Назад</h4></button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </selection>
    </body>
</html>
