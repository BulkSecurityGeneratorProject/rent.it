'use strict';

describe('Controller Tests', function() {

    describe('UserAddress Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockUserAddress;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockUserAddress = jasmine.createSpy('MockUserAddress');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'UserAddress': MockUserAddress
            };
            createController = function() {
                $injector.get('$controller')("UserAddressDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'rentitApp:userAddressUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
