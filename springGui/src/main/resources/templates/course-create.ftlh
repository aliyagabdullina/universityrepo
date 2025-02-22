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
                        <form action="/course/create" method="post">
                            <div class="input-block">
                                <h5>Курс</h5> <input type="text" id="course" name="name" placeholder="Введите курс"/><br>
                            </div>
                            <div class="input-block">
                                <h5>Уроков в неделю</h5><input type="text" name="numOfLessonsPerWeek" placeholder="Введите кол-во уроков"/><br>
                            </div>
                            <div class="input-block">
                                <h5>Дней в неделю</h5><input type="text" name="numOfDaysPerWeek" placeholder="Введите кол-во дней"/><br>
                            </div>
                            <div class="input-block">
                                <h5>Макс уроков в день</h5><input type="text" name="maxLessonsPerDay" placeholder="Введите кол-во уроков"/><br>
                            </div>
                            <div class="input-block">
                                <h5>Сложность</h5><input type="text" name="complexity" placeholder="Введите сложность"/><br>
                            </div>
                            <div class="input-block">
                                 <h5>Кабинет группы</h5>
                                 <div class="select-block">
                                     <select name="placeId" multiple>
                                         <option value="" disabled selected>Выберите кабинет</option>
                                         <#list places as place>
                                             <option value="${place.placeId}">${place.name}</option>
                                         </#list>
                                     </select><br>
                                 </div>
                             </div>
                            <div class="footer">
                                <div class="footer-buttons-container">
                                    <button type="submit" class="footer-button" onclick="window.location.href='/courses'">
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
