document.addEventListener('DOMContentLoaded', function () {
  fetch('/api/schedule')  // Запрос данных с сервера
    .then(response => response.json())
    .then(data => {
      console.log("Полученные данные: ", data);

      const teacherList = data.teachers;
      const studentList = data.students;
      const groupList = data.groups;
      const placeList = data.places;
      const groupScheduleMap = data.groupScheduleMap;
      const teacherScheduleMap = data.teacherScheduleMap;
      const placeScheduleMap = data.placeScheduleMap;
      const studentScheduleMap = data.studentScheduleMap;

      const categorySelect = document.getElementById('categorySelect');
      const searchInput = document.getElementById('searchInput');
      const resultsContainer = document.getElementById('results');
      const exportBtn = document.getElementById('exportBtn');

      // Кнопка экспорта изначально неактивна
      exportBtn.disabled = true;

      const categoryMap = {
        teacher: teacherList,
        student: studentList,
        group: groupList,
        place: placeList
      };

      categorySelect.addEventListener('change', () => {
        searchInput.disabled = false;
        searchInput.value = "";
        searchInput.placeholder = "Введите запрос...";
        resultsContainer.innerHTML = "";
        resultsContainer.style.display = "none";

        // Очистка расписания и блокировка кнопки экспорта
        document.getElementById("schedule-title").innerHTML = "";
        document.getElementById("schedule-table").innerHTML = "";
        exportBtn.disabled = true;
      });

      searchInput.addEventListener('input', () => {
        const query = searchInput.value.toLowerCase();
        const selectedCategory = categorySelect.value;
        const list = categoryMap[selectedCategory] || [];

        const results = list.filter(item =>
          item.name && item.name.toLowerCase().includes(query)
        );

        showResults(results, selectedCategory);
      });

      function showResults(results, entityType) {
        resultsContainer.innerHTML = "";

        if (results.length === 0) {
          resultsContainer.style.display = "none";
          return;
        }

        resultsContainer.style.display = "block";

        results.forEach(item => {
          const el = document.createElement("div");
          el.className = "result-item";
          el.textContent = item.name;
          el.onclick = () => {
            resultsContainer.style.display = "none";
            searchInput.value = item.name;

            // Рендер расписания по выбранной категории
            if (entityType === 'group') {
              renderSchedule("group", item.name, groupScheduleMap);
            } else if (entityType === 'teacher') {
              renderSchedule("teacher", item.name, teacherScheduleMap);
            } else if (entityType === 'place') {
              renderSchedule("place", item.name, placeScheduleMap);
            } else if (entityType === 'student') {
              renderSchedule("student", item.name, studentScheduleMap);
            }
          };
          resultsContainer.appendChild(el);
        });
      }

      const DAYS = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];
      const LESSONS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14];

      function renderSchedule(entityType, entityName, scheduleMap) {
        const schedule = scheduleMap[entityName];

        if (!schedule) {
          document.getElementById("schedule-table").innerHTML = "<p>Расписание не найдено</p>";
          document.getElementById("schedule-title").innerHTML = "";
          exportBtn.disabled = true;
          return;
        }

        let html = "<table><thead><tr><th>№</th>";
        DAYS.forEach(day => {
          html += `<th>${day}</th>`;
        });
        html += "</tr></thead><tbody>";

        LESSONS.forEach(lesson => {
          html += `<tr><th>${lesson}</th>`;
          DAYS.forEach(day => {
            const entry = schedule.find(e => e.dayOfWeek === day && e.lessonNumber === lesson);
            const content = entry ? `${entry.course}</div><div>${entry.place}</div><div>${entry.teacher}` : "";
            html += `<td>${content || "&nbsp;"}</td>`;
          });
          html += "</tr>";
        });

        html += "</tbody></table>";

        document.getElementById("schedule-title").innerHTML = `<h3>${entityName}</h3>`;
        document.getElementById("schedule-table").innerHTML = `<div class="table-wrapper">${html}</div>`;

        exportBtn.disabled = false; // разблокируем кнопку, т.к. расписание есть
      }

        function saveSchedulePDF() {
          const scheduleTable = document.querySelector('#schedule-table table');
          if (!scheduleTable) {
            alert('Расписание не найдено для экспорта');
            return;
          }

          html2canvas(scheduleTable, { scale: 2 }).then(canvas => {
            const imgData = canvas.toDataURL('image/png');
            const pdf = new jspdf.jsPDF('l', 'mm', 'a4'); // альбомный формат

            const pageWidth = pdf.internal.pageSize.getWidth();
            const pageHeight = pdf.internal.pageSize.getHeight();

            const imgProps = pdf.getImageProperties(imgData);
            const imgWidth = pageWidth - 20;
            const imgHeight = (imgProps.height * imgWidth) / imgProps.width;

            let heightLeft = imgHeight;
            let position = 10;

            pdf.addImage(imgData, 'PNG', 10, position, imgWidth, imgHeight);
            heightLeft -= pageHeight;

            while (heightLeft > 0) {
              pdf.addPage();
              position = heightLeft - imgHeight + 10;
              pdf.addImage(imgData, 'PNG', 10, position, imgWidth, imgHeight);
              heightLeft -= pageHeight;
            }

            pdf.save('schedule.pdf');
          }).catch(err => {
            console.error(err);
            alert('Ошибка при создании PDF');
          });
        }


      exportBtn.addEventListener('click', () => {
        saveSchedulePDF();
      });

      function saveSchedulePDF() {
        const scheduleTable = document.querySelector('#schedule-table table');
        if (!scheduleTable) {
          alert('Расписание не найдено для экспорта');
          return;
        }

        html2canvas(scheduleTable, {
          scale: 2,
          scrollY: -window.scrollY,
          useCORS: true
        }).then(canvas => {
          const imgData = canvas.toDataURL('image/png');
          const pdf = new jspdf.jsPDF('l', 'mm', 'a4');

          const pageWidth = pdf.internal.pageSize.getWidth();
          const pageHeight = pdf.internal.pageSize.getHeight();

          const imgProps = pdf.getImageProperties(imgData);
          const pdfWidth = pageWidth - 20;
          const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width;

          pdf.addImage(imgData, 'PNG', 10, 10, pdfWidth, pdfHeight);
          pdf.save('schedule.pdf');
        }).catch(err => {
          console.error(err);
          alert('Ошибка при создании PDF');
        });
      }
    });
});
