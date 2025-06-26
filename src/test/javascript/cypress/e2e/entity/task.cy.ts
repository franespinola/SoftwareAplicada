import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Task e2e test', () => {
  const taskPageUrl = '/task';
  const taskPageUrlPattern = new RegExp('/task(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const taskSample = { description: 'gust apologise', completed: false, createdAt: '2025-06-24T21:47:07.868Z' };

  let task;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/tasks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/tasks').as('postEntityRequest');
    cy.intercept('DELETE', '/api/tasks/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (task) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tasks/${task.id}`,
      }).then(() => {
        task = undefined;
      });
    }
  });

  it('Tasks menu should load Tasks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('task');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Task').should('exist');
    cy.url().should('match', taskPageUrlPattern);
  });

  describe('Task page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(taskPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Task page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/task/new$'));
        cy.getEntityCreateUpdateHeading('Task');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', taskPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/tasks',
          body: taskSample,
        }).then(({ body }) => {
          task = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/tasks+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/tasks?page=0&size=20>; rel="last",<http://localhost/api/tasks?page=0&size=20>; rel="first"',
              },
              body: [task],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(taskPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Task page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('task');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', taskPageUrlPattern);
      });

      it('edit button click should load edit Task page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Task');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', taskPageUrlPattern);
      });

      it('edit button click should load edit Task page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Task');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', taskPageUrlPattern);
      });

      it('last delete button click should delete instance of Task', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('task').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', taskPageUrlPattern);

        task = undefined;
      });
    });
  });

  describe('new Task page', () => {
    beforeEach(() => {
      cy.visit(`${taskPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Task');
    });

    it('should create an instance of Task', () => {
      cy.get(`[data-cy="description"]`).type('pish accentuate wherever');
      cy.get(`[data-cy="description"]`).should('have.value', 'pish accentuate wherever');

      cy.get(`[data-cy="completed"]`).should('not.be.checked');
      cy.get(`[data-cy="completed"]`).click();
      cy.get(`[data-cy="completed"]`).should('be.checked');

      cy.get(`[data-cy="createdAt"]`).type('2025-06-24T09:19');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-06-24T09:19');

      cy.get(`[data-cy="targetDate"]`).type('2025-06-24T01:38');
      cy.get(`[data-cy="targetDate"]`).blur();
      cy.get(`[data-cy="targetDate"]`).should('have.value', '2025-06-24T01:38');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        task = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', taskPageUrlPattern);
    });
  });

  // =====================================================
  // TESTS E2E ESPECÍFICOS PARA EL PROYECTO
  // =====================================================

  describe('E2E Tests - Project Requirements', () => {
    // Test 1: Login usando API y crear nueva tarea
    it('should login using API and create a new task successfully', () => {
      // Usar autenticación directa por API (requisito del proyecto)
      cy.authenticatedRequest({
        method: 'GET',
        url: '/api/account',
      }).then(({ body }) => {
        expect(body.login).to.exist;
        cy.log(`Authenticated as: ${body.login}`);
      });

      // Navegar a la página de tareas
      cy.visit(taskPageUrl);
      cy.wait('@entitiesRequest');

      // Crear nueva tarea
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Task');

      // Llenar formulario con datos específicos del proyecto
      const taskDescription = 'E2E Test Task - Created via Cypress';
      cy.get(`[data-cy="description"]`).clear().type(taskDescription);
      cy.get(`[data-cy="description"]`).should('have.value', taskDescription);

      // Dejar como no completada inicialmente
      cy.get(`[data-cy="completed"]`).should('not.be.checked');

      // Establecer fecha de creación
      const currentDateTime = new Date().toISOString().slice(0, 16);
      cy.get(`[data-cy="createdAt"]`).clear().type(currentDateTime);
      cy.get(`[data-cy="createdAt"]`).should('have.value', currentDateTime);

      // Establecer fecha objetivo (1 día después)
      const tomorrow = new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().slice(0, 16);
      cy.get(`[data-cy="targetDate"]`).clear().type(tomorrow);
      cy.get(`[data-cy="targetDate"]`).should('have.value', tomorrow);

      // Guardar la tarea
      cy.get(entityCreateSaveButtonSelector).click();

      // Verificar que se creó correctamente
      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        expect(response?.body.description).to.equal(taskDescription);
        expect(response?.body.completed).to.be.false;
        task = response.body;
        cy.log(`Task created with ID: ${task.id}`);
      });

      // Verificar que regresamos a la lista y aparece la tarea
      cy.wait('@entitiesRequest');
      cy.url().should('match', taskPageUrlPattern);
      cy.get(entityTableSelector).should('contain', taskDescription);
    });

    // Test 2: Marcar tarea como completada
    it('should mark an existing task as completed', () => {
      // Primero crear una tarea para el test
      const testTaskDescription = 'Task to be completed - E2E Test';
      cy.authenticatedRequest({
        method: 'POST',
        url: '/api/tasks',
        body: {
          description: testTaskDescription,
          completed: false,
          createdAt: new Date().toISOString(),
          targetDate: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString(),
        },
      }).then(({ body }) => {
        task = body;
        cy.log(`Test task created with ID: ${task.id}`);

        // Visitar la página de tareas
        cy.visit(taskPageUrl);
        cy.wait('@entitiesRequest');

        // Buscar la tarea específica y hacer clic en editar
        cy.get(entityTableSelector)
          .contains(testTaskDescription)
          .parents('tr')
          .within(() => {
            cy.get(entityEditButtonSelector).click();
          });

        // Verificar que estamos en la página de edición
        cy.getEntityCreateUpdateHeading('Task');

        // Verificar que inicialmente no está completada
        cy.get(`[data-cy="completed"]`).should('not.be.checked');

        // Marcar como completada
        cy.get(`[data-cy="completed"]`).click();
        cy.get(`[data-cy="completed"]`).should('be.checked');

        // Verificar que otros campos mantienen sus valores
        cy.get(`[data-cy="description"]`).should('have.value', testTaskDescription);

        // Guardar los cambios
        cy.get(entityCreateSaveButtonSelector).click();

        // Verificar que la actualización fue exitosa
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });

        // Verificar que regresamos a la lista
        cy.url().should('match', taskPageUrlPattern);

        // Verificar que la tarea aparece en la lista
        cy.get(entityTableSelector).should('contain', testTaskDescription);

        // Verificar usando API que efectivamente se marcó como completada
        cy.authenticatedRequest({
          method: 'GET',
          url: `/api/tasks/${task.id}`,
        }).then(({ body }) => {
          expect(body.completed).to.be.true;
          expect(body.description).to.equal(testTaskDescription);
          cy.log('Task successfully marked as completed!');
        });
      });
    });
  });
});
