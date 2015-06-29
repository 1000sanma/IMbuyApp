

<?php
$conn = mysqli_connect("localhost","root","xeriof156512","hi"); //加上@ 可抑制錯誤訊息
if(mysqli_connect_errno($conn))
{
	die("資料庫連線失敗");
}

mysqli_set_charset($conn,"utf8");

if($_POST['query_string']!="")
{
	$cate=$_POST['query_string'];
	$titleRows=mysqli_query($conn,"select title,content from title where type=$cate order by day DESC");

	$resultNum=5;
	$result=array();
	for($i=0;$i<$resultNum;$i++)
	{
		$row=mysqli_fetch_array($titleRows);
		$result[$i]=$row;		
	}
	print(json_encode($result)); 
}
?>

