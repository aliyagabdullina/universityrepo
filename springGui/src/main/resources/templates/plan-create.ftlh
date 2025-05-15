<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="/styles/styleDataPages.css">
        <link rel="stylesheet" href="/styles/styleCreate.css">
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
                        <h2>Редактирование данных об индивидульных планах</h2>
                        <button class="button-none" type="button" onclick="window.history.back()">
                            <img src="/images/back.svg" alt="Аккаунт" width="25" height="25">
                        </button>
                    </div>
                    <div class="main">
                        <form action="/teacher/create" method="post">
                            <div class="input-block">
                                <h5>Студенты</h5>
                                <div class="custom-multiselect" id="student-multiselect">
                                    <div class="selected-options">Выберите студентов</div>
                                    <div class="options-list">
                                        <#list students as student>
                                            <div class="option" data-value="${student.studentId}">${student.name}</div>
                                        </#list>
                                    </div>
                                    <input type="hidden" name="studentId" id="student-hidden-input">
                                </div>
                            </div>
                            <div class="spacer-bottom"></div>

                            <div class="input-block">
                                <h5>Программы</h5>
                                <div class="custom-multiselect" id="program-multiselect">
                                    <div class="selected-options">Выберите программы</div>
                                    <div class="options-list">
                                        <#list programs as program>
                                            <div class="option" data-value="${program.programId}">${program.name}</div>
                                        </#list>
                                    </div>
                                    <input type="hidden" name="programId" id="program-hidden-input">
                                </div>
                            </div>
                            <div class="spacer-bottom"></div>

                            <div class="input-block">
                                <h5>Дополнительные предметы</h5>
                                <div class="custom-multiselect" id="course-multiselect">
                                    <div class="selected-options">Выберите предметы</div>
                                    <div class="options-list">
                                        <#list courses as course>
                                            <div class="option" data-value="${course.courseId}">${course.name}</div>
                                        </#list>
                                    </div>
                                    <input type="hidden" name="courseId" id="course-hidden-input">
                                </div>
                            </div>
                            <div class="spacer-bottom"></div>


                            <div class="input-block">
                                <h5>Кол-во часов</h5>
                                    <input type="text" name="hours" placeholder="Введите кол-во часов" required
                                           value="<#if plan?? && plan.hours??>${plan.hours}</#if>">
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
        <script>
        document.addEventListener("DOMContentLoaded", function () {
            document.querySelectorAll(".custom-select-data select").forEach((originalSelect) => {
                const customSelect = document.createElement("div");
                customSelect.classList.add("custom-multiselect");

                const selectedItems = document.createElement("div");
                selectedItems.classList.add("selected-items");
                customSelect.appendChild(selectedItems);

                const optionsList = document.createElement("div");
                optionsList.classList.add("options-list");

                Array.from(originalSelect.options).forEach((option) => {
                    if (option.disabled) return;
                    const optionItem = document.createElement("div");
                    optionItem.textContent = option.text;
                    optionItem.dataset.value = option.value;

                    optionItem.addEventListener("click", () => {
                        option.selected = !option.selected;
                        optionItem.classList.toggle("selected");

                        updateSelectedItems();
                    });

                    optionsList.appendChild(optionItem);
                });

                function updateSelectedItems() {
                    selectedItems.innerHTML = "";

                    Array.from(originalSelect.selectedOptions).forEach((option) => {
                        const item = document.createElement("span");
                        item.textContent = option.text;
                        selectedItems.appendChild(item);
                    });
                }

                updateSelectedItems();

                selectedItems.addEventListener("click", () => {
                    optionsList.classList.toggle("show");
                });

                customSelect.appendChild(optionsList);

                originalSelect.style.display = "none";
                originalSelect.parentNode.insertBefore(customSelect, originalSelect);
            });

            // Закрытие всех списков по клику вне
            document.addEventListener("click", (e) => {
                document.querySelectorAll(".custom-multiselect .options-list").forEach((list) => {
                    if (!list.parentNode.contains(e.target)) {
                        list.classList.remove("show");
                    }
                });
            });
        });
        </script>
    </body>
</html>
