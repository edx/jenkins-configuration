from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from configuration_page import ConfigurationSubPageMixIn

class MaskPasswordsSubPage(ConfigurationSubPageMixIn, PageObject):

    def __init__(self, *args, **kwargs):
        super(MaskPasswordsSubPage, self).__init__(*args, **kwargs)
        self.name = "com-michelin-cio-hudson-plugins-maskpasswords-MaskPasswordsBuildWrapper"

    def get_selected_masked_password_classes(self):
        class_names = self.values_of_elements_named('maskedParamDefs')
        selected_classes = self.q(css='[name="{}"]'.format('selectedMaskedParamDefs')).attrs('checked')
        masked_password_classes = []
        for name, selected in zip(class_names, selected_classes):
            if selected == 'true':
                masked_password_classes.append(name)
        return masked_password_classes

    def get_global_var_values(self):
        return self.values_of_elements_named('globalVarPasswordPair.var')
