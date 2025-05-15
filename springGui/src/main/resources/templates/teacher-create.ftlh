<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="/styles/styleDataWithTimeSlots.css">
    </head>
    <body>
        <section>
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
                        <h2>Редактирование данных о преподавателях</h2>
                        <button class="button-none" type="button" onclick="window.history.back()">
                            <img src="/images/back.svg" alt="Аккаунт" width="25" height="25">
                        </button>
                    </div>
                    <div class="main">
                        <form action="/teacher/create" method="post">
                          <div class="input-grid">
                            <div class="input-block">
                                <h5>ФИО преподавателя</h5>
                                    <input type="text" name="name" placeholder="Введите ФИО" required
                                           value="<#if teacher?? && teacher.name??>${teacher.name}</#if>">
                            </div>

                            <div class="input-block">
                                <h5>Почта</h5>
                                    <input type="text" name="email" placeholder="Введите почту" required
                                           value="<#if teacher?? && teacher.email??>${teacher.email}</#if>">
                            </div>







                             <div class="input-block">
                               <h5>Предметы</h5>
                               <div class="custom-multiselect" id="courseSelector">
                                 <div class="selected-options">
                                   <#if teacher?? && teacher.courses?? && (teacher.courses?size > 0)>
                                     ${teacher.courses?map(c -> c.name)?join(", ")}
                                   <#else>
                                     Выберите предметы
                                   </#if>
                                 </div>
                                 <div class="options-list hidden">
                                   <#list courses as course>
                                     <#assign isSelected = (teacher?? && teacher.courses?? && teacher.courses?seq_contains(course))>
                                     <div class="option-item <#if isSelected>selected</#if>" data-id="${course.courseId}">
                                       ${course.name}
                                     </div>
                                   </#list>
                                 </div>
                               </div>
                               <input type="hidden" name="courseId" id="selectedCourses"
                                      value="<#if teacher?? && teacher.courses??>${teacher.courses?map(c -> c.courseId)?join(",")}</#if>">
                             </div>






                            <div class="input-block">
                                <h5>Кол-во пар в день</h5>
                                    <input type="text" name="maxNumberOfLessons" placeholder="Введите кол-во пар в день" required
                                           value="<#if teacher?? && teacher.maxNumberOfLessons??>${teacher.maxNumberOfLessons}</#if>">
                            </div>

                          <div class="input-block">
                              <h5>Должность</h5>
                                  <input type="text" name="degree" placeholder="Введите должность" required
                                         value="<#if teacher?? && teacher.degree??>${teacher.degree}</#if>">
                          </div>
                            <div class="spacer-bottom"></div>
                            <div class="input-block">
                              <h5>Доступные временные слоты</h5>
                              <div class="availability-grid">
                                <table>
                                  <thead>
                                    <tr>
                                      <th></th>
                                      <th>ПН</th>
                                      <th>ВТ</th>
                                      <th>СР</th>
                                      <th>ЧТ</th>
                                      <th>ПТ</th>
                                      <th>СБ</th>
                                    </tr>
                                  </thead>
                                  <tbody id="grid-body">
                                    <!-- сюда будет вставлена таблица -->
                                  </tbody>
                                </table>
                                <div class="color-legend">
                                  <div class="color-option-wrapper" data-tooltip="Не подходит">
                                    <div class="color-option level-2" data-level="3"></div>
                                  </div>
                                  <div class="color-option-wrapper" data-tooltip="Не желательно">
                                    <div class="color-option level-1" data-level="2"></div>
                                  </div>
                                  <div class="color-option-wrapper" data-tooltip="Желательно">
                                    <div class="color-option level-0" data-level="1"></div>
                                  </div>
                                  <div class="color-option-wrapper" data-tooltip="Ничего не выбрано">
                                    <div class="color-option level-disabled" data-level="0"></div>
                                  </div>
                                </div>
                              </div>
                            </div>

                            <!-- скрытое поле для отправки -->
                            <input type="hidden" name="availabilityGrid" id="availabilityGrid">



                        </form>
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
                </div>
            </div>
        </section>
        <script>
          document.addEventListener("DOMContentLoaded", () => {
            const days = ['ПН', 'ВТ', 'СР', 'ЧТ', 'ПТ', 'СБ'];
            const lessons = 7;
            const gridBody = document.getElementById('grid-body');
            const selectedColorInput = document.getElementById("selectedColorLevel");
            const hiddenInput = document.getElementById("availabilityGrid");
            const colorOptions = document.querySelectorAll(".color-option");
            let selectedLevel = "0";

            // Сгенерировать таблицу
            for (let i = 1; i <= lessons; i++) {
              const tr = document.createElement('tr');
              const numberCell = document.createElement('td');
              numberCell.textContent = i;
              tr.appendChild(numberCell);

              for (let j = 0; j < days.length; j++) {
                const td = document.createElement('td');
                td.classList.add('cell');
                td.dataset.level = "0";
                td.dataset.day = j;
                td.dataset.lesson = i;
                tr.appendChild(td);
              }
              gridBody.appendChild(tr);
            }

            // Переключение выбранного цвета
            colorOptions.forEach(option => {
              option.addEventListener("click", () => {
                colorOptions.forEach(opt => opt.classList.remove("selected"));
                option.classList.add("selected");
                selectedLevel = option.getAttribute("data-level");
              });
            });

            // Клик по ячейке: назначаем цвет и сохраняем данные
            gridBody.addEventListener("click", (e) => {
              if (e.target.classList.contains('cell')) {
                e.target.dataset.level = selectedLevel;
                updateHiddenInput();
              }
            });

            function updateHiddenInput() {
              const data = [];
              const cells = document.querySelectorAll('.cell');
              cells.forEach(cell => {
                data.push({
                  day: parseInt(cell.dataset.day),
                  lesson: parseInt(cell.dataset.lesson),
                  level: parseInt(cell.dataset.level)
                });
              });
              hiddenInput.value = JSON.stringify(data);
            }

            // начальное обновление
            updateHiddenInput();
          });
          document.addEventListener("DOMContentLoaded", () => {
          const multiselect = document.getElementById("courseSelector");
          const selectedDiv = multiselect.querySelector(".selected-options");
          const optionsList = multiselect.querySelector(".options-list");
          const hiddenInput = document.getElementById("selectedCourses");
          const selectedIds = new Set();

          // Открыть/закрыть список
          selectedDiv.addEventListener("click", (e) => {
            optionsList.classList.toggle("hidden");
          });

          // Выбор/отмена выбора
          optionsList.addEventListener("click", (e) => {
            if (e.target.classList.contains("option-item")) {
              const id = e.target.dataset.id;
              if (selectedIds.has(id)) {
                selectedIds.delete(id);
                e.target.classList.remove("selected");
              } else {
                selectedIds.add(id);
                e.target.classList.add("selected");
              }
              updateSelectedDisplay();
            }
          });

          function updateSelectedDisplay() {
            if (selectedIds.size === 0) {
              selectedDiv.textContent = "Выберите предметы";
            } else {
              const items = Array.from(optionsList.querySelectorAll(".option-item"))
                .filter(item => selectedIds.has(item.dataset.id))
                .map(item => item.textContent);
              selectedDiv.textContent = items.join(", ");
            }
            hiddenInput.value = Array.from(selectedIds).join(",");
          }

          // Закрытие при клике вне
          document.addEventListener("click", (e) => {
            if (!multiselect.contains(e.target)) {
              optionsList.classList.add("hidden");
            }
          });
        });

        const preselectedCourses = [
            <#if teacher?? && teacher.courses??>
              <#list teacher.courses as c>"${c.courseId}"<#if c_has_next>, </#if></#list>
            </#if>
          ];

          <script>
          document.addEventListener("DOMContentLoaded", () => {
            const multiselect = document.getElementById("courseSelector");
            const selectedDiv = multiselect.querySelector(".selected-options");
            const optionsList = multiselect.querySelector(".options-list");
            const hiddenInput = document.getElementById("selectedCourses");

            const selectedIds = new Set();

            // Заполнение selectedIds, если есть предзаполненные курсы
            const preselected = hiddenInput.value.split(",").map(v => v.trim()).filter(Boolean);
            preselected.forEach(id => selectedIds.add(id));

            // Применить предзаполненные стили
            const optionItems = optionsList.querySelectorAll(".option-item");
            optionItems.forEach(item => {
              if (selectedIds.has(item.dataset.id)) {
                item.classList.add("selected");
              }
            });
            updateSelectedDisplay();

            // Открыть/закрыть список
            selectedDiv.addEventListener("click", () => {
              optionsList.classList.toggle("hidden");
            });

            // Клик по предмету
            optionsList.addEventListener("click", (e) => {
              if (e.target.classList.contains("option-item")) {
                const id = e.target.dataset.id;
                if (selectedIds.has(id)) {
                  selectedIds.delete(id);
                  e.target.classList.remove("selected");
                } else {
                  selectedIds.add(id);
                  e.target.classList.add("selected");
                }
                updateSelectedDisplay();
              }
            });

            // Обновить видимость и скрытое поле
            function updateSelectedDisplay() {
              if (selectedIds.size === 0) {

        </script>
    </body>
</html>
