
        if (localStorage.getItem("review") === "true") {
            $("#review-part").html(
                "<h1 class=\"likert-header\">Leave a review for the paper</h1>\n" +
                "                <div class=\"clear\"></div>\n" +
                "                <label style=\"color: white\"> .</label>\n" +
                "                <form action=\"\" id=\"reviewer-bid\">\n" +
                "                    <ul class='likert'>\n" +
                "                        <li>\n" +
                "                            <input type=\"radio\" name=\"likert\" value=\"strongReject\" class=\"gradeInput\">\n" +
                "                            <label>Strong Reject</label>\n" +
                "                        </li>\n" +
                "                        <li>\n" +
                "                            <input type=\"radio\" name=\"likert\" value=\"reject\" class=\"gradeInput\">\n" +
                "                            <label>Reject</label>\n" +
                "                        </li>\n" +
                "                        <li>\n" +
                "                            <input type=\"radio\" name=\"likert\" value=\"weakReject\" class=\"gradeInput\">\n" +
                "                            <label>Weak Reject</label>\n" +
                "                        </li>\n" +
                "                        <li>\n" +
                "                            <input type=\"radio\" name=\"likert\" value=\"borderlinePaper\" class=\"gradeInput\">\n" +
                "                            <label>Borderline</label>\n" +
                "                        </li>\n" +
                "                        <li>\n" +
                "                            <input  type=\"radio\" name=\"likert\" value=\"weakAccept\" class=\"gradeInput\">\n" +
                "                            <label>Weak Accept</label>\n" +
                "                        </li>\n" +
                "                        <li>\n" +
                "                            <input type=\"radio\" name=\"likert\" value=\"accept\" class=\"gradeInput\">\n" +
                "                            <label>Accept</label>\n" +
                "                        </li>\n" +
                "                        <li>\n" +
                "                            <input type=\"radio\" name=\"likert\" value=\"strongAccept\" class=\"gradeInput\">\n" +
                "                            <label>Strong Accept</label>\n" +
                "                        </li>\n" +
                "                    </ul>\n" +
                "\n" +
                "                    <div class=\"block clear\">\n" +
                "                        <h1 class=\"likert-header\" >Feel free to leave a recommendation</h1>\n" +
                "                        <textarea name=\"recomand\" id=\"recomand\" cols=\"80\" rows=\"10\" style=\"margin: 20px 0px 40px 0px;font-size:20px;\"></textarea>\n" +
                "                    </div>\n" +
                "\n" +
                "                    <div class=\"clear\"></div>\n" +
                "\n" +
                "                    <div>\n" +
                "                        <button id=\"clearRevButton\" class=\"btn\"  type=\"reset\">\n" +
                "                            Clear\n" +
                "                        </button>\n" +
                "                        <button id=\"submitRevButton trigg-confirm\" class=\"btn\"  type=\"button\"\n" +
                "                            onclick=\"reviewPaper()\">\n" +
                "                            Submit\n" +
                "                        </button>\n" +
                "\n" +
                "                        <div id=\"rev-pop\" class=\"rev-pop\">\n" +
                "                            <div class=\"rev-pop-content\">\n" +
                "                                <div class=\"rev-pop-header\">\n" +
                "                                    <span class=\"close\">&times;</span>\n" +
                "                                    <h2>Registration was confirmed</h2>\n" +
                "                                </div>\n" +
                "                                <div class=\"rev-pop-body\">\n" +
                "                                    <p>You were sent a confirmation email.</p>\n" +
                "                                </div>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "\n" +
                "                    </div>\n" +
                "                </form>"
            );


        }
