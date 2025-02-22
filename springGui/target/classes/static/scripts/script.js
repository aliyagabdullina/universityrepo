function updateSchedule() {
    var selectedGroup = document.getElementById("groupSelect").value;

    // Перенаправить пользователя на адрес с выбранным именем группы в качестве параметра пути
    window.location.href = '/schedule/group/' + encodeURIComponent(selectedGroup);
}
