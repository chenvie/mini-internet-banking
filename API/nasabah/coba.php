<?php
$a = 'asd';
if (strpos($a,' ') == null){
    $asd = $a;
}else {
    $asd = substr($a, 0, strpos($a, ' '));
}
echo $asd;
?>