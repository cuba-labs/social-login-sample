package com.haulmont.sample.petclinic.web.pet.pettype;

import com.haulmont.cuba.gui.Route;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.sample.petclinic.entity.pet.PetType;

@Route("pet-types")
@UiController("petclinic_PetType.browse")
@UiDescriptor("pet-type-browse.xml")
@LookupComponent("petTypesTable")
@LoadDataBeforeShow
public class PetTypeBrowse extends StandardLookup<PetType> {
}
