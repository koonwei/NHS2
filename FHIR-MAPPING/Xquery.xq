declare default element namespace "http://schemas.openehr.org/v1";

declare function local:getXML(){
  for $open in distinct-values(doc("Smart_Growth_Chart_Data.v0.opt")/template/definition/attributes/children/archetype_id)
  let $openTemplate := $open
    for $open2 in doc("Smart_Growth_Chart_Data.v0.opt")/template/definition/attributes
     where $open2/children[archetype_id = $openTemplate]
     let $baseNodePath := $open2/children[@xsi:type = "C_ARCHETYPE_ROOT" and archetype_id = $openTemplate]/attributes/children[@xsi:type = "C_COMPLEX_OBJECT" and (rm_type_name = "POINT_EVENT" or rm_type_name = "HISTORY")]
     let $dataNode := $baseNodePath/node_id
     let $eventNode := $baseNodePath/attributes[@xsi:type = "C_MULTIPLE_ATTRIBUTE"]/children[@xsi:type = "C_COMPLEX_OBJECT"]/node_id
     let $dataValueNode := $baseNodePath/attributes[@xsi:type = "C_MULTIPLE_ATTRIBUTE"]/children[@xsi:type = "C_COMPLEX_OBJECT"]/attributes[@xsi:type = "C_SINGLE_ATTRIBUTE"]/children[@xsi:type = "C_COMPLEX_OBJECT" and rm_type_name = "ITEM_TREE"]/attributes[@xsi:type = "C_MULTIPLE_ATTRIBUTE"]/children[@xsi:type="C_COMPLEX_OBJECT"]/../../node_id
     let $itemNode :=  $baseNodePath/attributes[@xsi:type = "C_MULTIPLE_ATTRIBUTE"]/children[@xsi:type = "C_COMPLEX_OBJECT"]/attributes[@xsi:type = "C_SINGLE_ATTRIBUTE"]/children[@xsi:type = "C_COMPLEX_OBJECT" and rm_type_name = "ITEM_TREE"]/attributes[@xsi:type="C_MULTIPLE_ATTRIBUTE"]/children[@xsi:type="C_COMPLEX_OBJECT"]/node_id
     let $dataNode := distinct-values($dataNode)
     let $eventNode := distinct-values($eventNode)
     let $dataValueNode := distinct-values($dataValueNode)
     let $itemNode := distinct-values($itemNode)
     return
     <type>
      <archetype>{$open}</archetype>
      <path>
      <date>data[{data($dataNode)}]/origin/value</date>
      <magnitude>data[{data($dataNode)}]/events[{data($eventNode)}]/data[{data($dataValueNode)}]/items[{data($itemNode)}]/value/magnitude</magnitude>
      <units>data[{data($dataNode)}]/events[{data($eventNode)}]/data[{data($dataValueNode)}]/items[{data($itemNode)}]/value/units</units>
      </path>
      </type>
};
local:getXML()