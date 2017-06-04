
$jade_path="C:\Users\marcin\Documents\jade_test\lib\jade.jar"
$src_path="C:\Users\marcin\Documents\jade_test\out"
$player_class="HelloAgent"
$broker_class="HelloAgent"
$banker_class="HelloAgent"
$class_path = $jade_path + ";" + $src_path
$container_name = "Stock market"
$agents_array = [System.Collections.ArrayList]@()

$agents_names=@(
    "test1",
    "test2"
)

$agents_config=@(1,2)

#strategy 1...
for ( $i = 0; $i -lt $args[0]; $i++) {
    $agents_array.Add($agents_names[0] + "_" + $i + ":" + $player_class + "(" + $i + ")")
}

#strategy 2...
for ( $i = 0; $i -lt $args[1]; $i++) {
  #Write-Host $i  
}

#strategy 3...
for ( $i = 0; $i -lt $args[2]; $i++) {
  #Write-Host $i  
}

$agents_array.Add("Banker:" + $banker_class + "(" + $i + ")")
$agents_array.Add("Broker:" + $broker_class + "(" + $i + ")")

$agents_list = $agents_array -Join ';' 

java -cp $class_path jade.Boot -container -container-name $container_name -agents $agents_list

