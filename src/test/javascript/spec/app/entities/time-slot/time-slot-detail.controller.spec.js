'use strict';

describe('Controller Tests', function() {

    describe('TimeSlot Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTimeSlot;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTimeSlot = jasmine.createSpy('MockTimeSlot');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'TimeSlot': MockTimeSlot
            };
            createController = function() {
                $injector.get('$controller')("TimeSlotDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'rentitApp:timeSlotUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
