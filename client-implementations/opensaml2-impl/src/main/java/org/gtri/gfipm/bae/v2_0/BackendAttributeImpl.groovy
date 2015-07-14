package org.gtri.gfipm.bae.v2_0


class BackendAttributeImpl implements BackendAttribute {


    String name;
    String friendlyName;
    AttributeNameFormat nameFormat;
    List<BackendAttributeValue> values = [];

    BackendAttributeValue getValue() {
        if( values && values.size() > 0 )
            return values.get(0);
        else
            return null;
    }

    String getFriendlyName(){
        friendlyName ? friendlyName : name
    }


}
