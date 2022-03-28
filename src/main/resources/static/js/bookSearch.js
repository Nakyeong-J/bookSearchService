var noInfo = '정보없음';

var Main = {
    addEvent: function () {
        $('button').bind('click', function (event) {
            Main.eventControl(event);
        });
    },

    eventControl: function (event) {
        var target = event.currentTarget;
        switch (target.id) {
            case "btnSearch":
                $("#currentPageNumber").val(1);
                Main.search(true);
                break;
            case "btnReturnToList":
                Main.search(false);
                break;

        }
    },

    /** init design */
    initDesign: function () {
        $('#searchKeyword').on('keypress', function (event) {
            if (event.keyCode == 13) {
                $("#currentPageNumber").val(1);
                Main.search(true);
                return false;
            }
            return;
        });
    },

    searchBoard: function (){
        $('#searchKeyword').val("");
        $('[name=menu]').removeClass("active");
        $('#searchMenu').addClass("active");
        $('#searchKeyword').val();
        $('tr[name=bookList]').remove();
        $("#bookDetailDiv").hide();
        $("#topRankKeyword").hide();
        $("#searchHistoryByUser").hide();
        $("#noList").hide();
        $("#pagination").hide();
        $("#searchBook").show();
    },

    search: function (historyYn) {

        if ($('#searchKeyword').val() == '') {
            alert("검색어를 입력해주세요.");
            return false;
        }

        var data = $('#mainForm').serializeArray();
        data.push({name:"historyYn", value:historyYn});

        $.ajax({
            url: "/searchBook",
            type: "GET",
            data: data,
            beforeSend: function (xhr) {
                var jwt = sessionStorage.getItem("Token");
                xhr.setRequestHeader("Authorization", 'Bearer '+ jwt);
            },
            success: function (res) {
                $('tr[name=bookList]').remove();
                var html = "";
                if (res.meta.total_count < 1) {
                    $("#searchList").hide();
                    $("#pagination").hide();
                    $("#noList").show();
                }else{
                    $(res.documents).each(
                        function (idx) {
                            if (isNull(this.title)) return true;
                            var authors = "";
                            var authorsCnt = this.authors.length;

                            $(this.authors)
                                .each(
                                    function (idx) {
                                        if (idx == authorsCnt - 1) {
                                            authors += this;
                                        } else {
                                            authors += (this + ", ");
                                        }
                                    });
                            this.authors = authors;
                            if (this.sale_price == '-1') {
                                this.sale_price = '정보없음';
                            }

                            html += "<tr onClick='Main.clickDetailInfo(" + JSON.stringify(this) + ")' name='bookList'>";
                            html +=     "<td class='ellipsis'>" + this.title + "</td>";
                            html +=     "<td class='ellipsis'>";
                            html +=         (isNull(this.authors)) ? noInfo : this.authors;
                            html +=     "</td>";
                            html +=     "<td class='ellipsis'>";
                            html +=         (isNull(this.publisher)) ? noInfo : this.publisher;
                            html +=     "</td>";
                            html +=     "<td class='ellipsis'>";
                            html +=         (isNull(this.sale_price)) ? noInfo : this.sale_price.toLocaleString();
                            html +=     "</td>";
                            html += "</tr>";
                        })
                    $("#searchList").append(html);

                    $("#noList").hide();
                    $("#searchList").show();
                    $("#bookDetailDiv").hide();
                    $("#topRankKeyword").hide();
                    $("#searchHistoryByUser").hide();
                    $("#pagination").show();
                    $("#searchBook").show();

                    Main.makePagination(res.meta);
                };
            },
            error: function(request,status,error){
                if(request.status == "401" || request.status == 401){
                    alert("인증이 만료되었습니다. 다시 로그인 해주세요.");
                    location.href = "/login";
                }else{
                    alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
                }
            }
        });

    },

    clickDetailInfo: function (bookDetail) {
        console.log(bookDetail);
        $('tr[name=bookDetail]').remove();

        var html = "";
        html += "<tr style='height: 30px' name='bookDetail'>";
        html += "<td rowspan='3'><img src='" + bookDetail.thumbnail + "' width='100'></td>";
        html += "<th>제목</th>";
        html += "<td class='ellipsis' colspan='3'>" + bookDetail.title + "</td>";
        html += "<th>작가</th>";
        html += "<td class='ellipsis'>";
        html += (isNull(bookDetail.authors)) ? noInfo : bookDetail.authors;
        html += "</td>";
        html += "</tr>";

        html += "<tr style='height: 30px' name='bookDetail'>";
        html += "<th>출판사</th>";
        html += "<td class='ellipsis' colspan='3'>";
        html += (isNull(bookDetail.publisher)) ? noInfo : bookDetail.publisher;
        html += "</td>";
        html += "<th>출판일</th>";
        html += "<td class='ellipsis'>";
        html += (isNull(bookDetail.dateTime)) ? noInfo : bookDetail.dateTime;
        html += "</td>";
        html += "</tr>";

        html += "<tr style='height: 30px' name='bookDetail'>";
        html += "<th>정가</th>";
        html += "<td>";
        html += (isNull(bookDetail.price)) ? noInfo : bookDetail.price.toLocaleString();
        html += "</td>";
        html += "<th>판매가</th>";
        html += "<td>";
        html += (isNull(bookDetail.salePrice)) ? noInfo : bookDetail.salePrice.toLocaleString();
        html += "</td>";
        html += "<th>ISBN</th>";
        html += "<td class='ellipsis'>";
        html += (isNull(bookDetail.isbn)) ? noInfo : bookDetail.isbn;
        html += "</td>";
        html += "</tr>";

        html += "<tr style='height: 30px' name='bookDetail'>";
        html += "<th>소개</th>";
        html += "<td colspan='6'>";
        html += (isNull(bookDetail.contents)) ? noInfo : bookDetail.contents;
        html += "</td>";
        html += "</tr>";

        $("#bookDetail").append(html);

        $("#searchBook").hide();
        $("#topRankKeyword").hide();
        $("#searchHistoryByUser").hide();
        $("#bookDetailDiv").show();
    },

    makePagination: function (metaInfo) {

        if (metaInfo.pageable_count % 10 == 0) {
            $("#totalPageNumber").val(metaInfo.pageable_count / 10);
        } else {
            $("#totalPageNumber").val(Math.floor(metaInfo.pageable_count / 10) + 1);
        }

        var totalPageNumber = $("#totalPageNumber").val();

        var currentPageNumber = $("#currentPageNumber").val();

        var firstNumber = Math.floor(currentPageNumber / 10) * 10;
        if(currentPageNumber%10 == 0 && Math.floor(currentPageNumber/10) > 0) firstNumber =  (Math.floor(currentPageNumber / 10)-1) * 10;
        var lastNumber = (Math.floor(currentPageNumber / 10) >= Math.floor(totalPageNumber / 10)) ? totalPageNumber : firstNumber + 10;

        var html = "";
        html += "<li name ='pageIcon'><a onclick='Main.previousPageSearch()'>&laquo;</a> </li>"
        for (var i = firstNumber + 1; i <= lastNumber; i++) {
            if (i == currentPageNumber) {
                html += "<li class='active' id='pageNum'" + i + " name ='pageIcon' ><a onclick='Main.pageSearch(" + i + ")'>" + i + "</li>"
            } else {
                html += "<li id='pageNum'" + i + " name ='pageIcon' ><a onclick='Main.pageSearch(" + i + ")'>" + i + "</li>"
            }
        }
        html += "<li name ='pageIcon'><a onclick='Main.nextPageSearch()'>&raquo;</a></li>"

        $('li[name=pageIcon]').remove();
        $("#pagination").append(html);

    },

    pageSearch: function (pageNumber) {
        $("#currentPageNumber").val(pageNumber);
        Main.search(false);
    },

    previousPageSearch: function(){
        var currentPageNumber =  $("#currentPageNumber").val();

        if(parseInt(currentPageNumber) > 1) {
            $("#currentPageNumber").val(currentPageNumber-1);
        }else{
            alert("첫 페이지 입니다.");
            return;
        }
        Main.search(false);
    },

    nextPageSearch: function(){
        var currentPageNumber =  $("#currentPageNumber").val();
        var totalPageNumber = $("#totalPageNumber").val();

        if(parseInt(currentPageNumber) < parseInt(totalPageNumber)) {
            $("#currentPageNumber").val(parseInt(currentPageNumber)+1);
        }else{
            alert("마지막 페이지 입니다.");
            return;
        }

        Main.search(false);
    },

    searchHistory : function (){
        $('[name=menu]').removeClass("active");
        $('#historyMenu').addClass("active");
        $.ajax({
            url: "/getHistoryByUser",
            type: "GET",
            beforeSend: function (xhr) {
                var jwt = sessionStorage.getItem("Token");
                xhr.setRequestHeader("Authorization", 'Bearer '+ jwt);
            },
            success: function (res) {

                $('tr[name=historyList]').remove();

                var html = "";
                $(res)
                    .each(
                        function () {
                            var date = new Date(this.regdt);
                            var dateTime = date.getFullYear() + "-" + (date.getMonth()+1) + "-" + date.getDate();
                            dateTime += " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();

                            html += "<tr name='historyList'>";
                            html +=     "<td class='ellipsis'>" + this.keyword + "</td>";
                            html +=     "<td class='ellipsis'>" + dateTime + "</td>";
                            html += "</tr>";
                        });
                $("#historyList").append(html);

                $("#searchBook").hide();
                $("#bookDetailDiv").hide();
                $("#topRankKeyword").hide();
                $("#searchHistoryByUser").show();

            },
            error: function(request,status,error){
                if(request.status == "401" || request.status == 401){
                    alert("인증이 만료되었습니다. 다시 로그인 해주세요.");
                    location.href = "/login";
                }else{
                    alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
                }
            }
        });
    },

    searchTopRank : function (){
        $('[name=menu]').removeClass("active");
        $('#rankMenu').addClass("active");
        $.ajax({
            url: "/getTopRankKeyword",
            type: "GET",
            beforeSend: function (xhr) {
                var jwt = sessionStorage.getItem("Token");
                xhr.setRequestHeader("Authorization", 'Bearer '+ jwt);
            },
            success: function (res) {
                $('tr[name=topRankRow]').remove();

                var html = "";
                $(res)
                    .each(
                        function () {
                            console.log(this);
                            html += "<tr name='topRankRow'>";
                            html +=     "<td class='ellipsis'>" + this.RANK + "</td>";
                            html +=     "<td class='ellipsis'>" + this.KEYWORD + "</td>";
                            html +=     "<td class='ellipsis'>" + this.CNT + "</td>";
                            html += "</tr>";
                        });
                $("#topRankList").append(html);

                $("#searchBook").hide();
                $("#bookDetailDiv").hide();
                $("#searchHistoryByUser").hide();
                $("#topRankKeyword").show();

            },
            error: function(request,status,error){
                if(request.status == "401" || request.status == 401){
                    alert("인증이 만료되었습니다. 다시 로그인 해주세요.");
                    location.href = "/login";
                }else{
                    alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
                }
            }
        });
    },

    logout: function (){
        sessionStorage.removeItem("Token");
        location.href = "/login";
    },
};

isNull = function (data) {
    if (data == null || data == "") return true;
    else return false;

};

$(function () {
    if(sessionStorage.getItem("Token") == null || sessionStorage.getItem("Token") == ""){
        location.href = "/login";
    }
    Main.addEvent();
    Main.initDesign();
});