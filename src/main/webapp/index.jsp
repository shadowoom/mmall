<html>
<body>
<h2>Hello World!</h2>


springmvc file upload
<form name="form1" action="/manage/product/upload.do" method="POST" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="spring mvc upload" />
</form>

rich text file upload
<form name="form1" action="/manage/product/richtext_img_upload.do" method="POST" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="rich text upload" />
</form>

</body>
</html>
