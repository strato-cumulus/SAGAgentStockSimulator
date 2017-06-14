
$project_path=$PSScriptRoot+"\.."
$jade_path=$project_path+"\lib\jade.jar"
$out_path=$project_path+"\out\production\SAGAgentStockSimulator"
$properties_path=$project_path+"shares.properties"
$player_class="agent.PlayerAgent"
$broker_class="agent.BrokerAgent"
$banker_class="agent.BankAgent"
$transactionmanager_class="agent.TransactionManager"
$class_path = $jade_path + ";" + $out_path + ";" + $project_path+"\lib\gson-2.8.1.jar"
$container_name = "Stock market"
$agents_array = [System.Collections.ArrayList]@()

$min_funds = 2000
$max_funds = 3000

$game_length = 100

$stock_data = Import-Csv $properties_path -delimiter ","
$i = 0;
Foreach($stock in $stock_data) {
    $agents_array.Add($stock[0] + "_" + $i + ":" + $transactionmanager_class + "(OnFallBuying, OnFallSelling, " + 0 + ")")
    $i = $i + 1
}

$agents_names=@(
    "InformationResponding",
    "OnFallBuying",
    "OnRiseBuying"
)

$agents_config=@(1,2)

#InformationResponding
for ( $i = 0; $i -lt $args[0]; $i++) {
	$cash = Get-Random -minimum $min_funds -maximum $max_funds
    $agents_array.Add($agents_names[0] + "_" + $i + ":" + $player_class + "(InformationResponding, OnFallSelling, " + $cash + ")")
}

#OnFallBuying
for ( $i = 0; $i -lt $args[0]; $i++) {
	$cash = Get-Random -minimum $min_funds -maximum $max_funds
    $agents_array.Add($agents_names[1] + "_" + $i + ":" + $player_class + "(OnFallBuying, OnFallSelling, " + $cash + ")")
}

#OnRiseBuying
for ( $i = 0; $i -lt $args[0]; $i++) {
	$cash = Get-Random -minimum $min_funds -maximum $max_funds
    $agents_array.Add($agents_names[2] + "_" + $i + ":" + $player_class + "(OnRiseBuying, OnFallSelling, " + $cash + ")")
}


$agents_array.Add("bank-0:" + $banker_class)
$agents_array.Add("broker-0:" + $broker_class + "(" + $game_length + ")")

$agents_list = $agents_array -Join ';' 

java -cp $class_path jade.Boot -container -container-name $container_name -agents $agents_list

